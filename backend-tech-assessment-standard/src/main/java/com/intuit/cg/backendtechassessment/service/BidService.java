package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.AutoBidDTO;
import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.model.AutoBid;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.AutoBidRepository;
import com.intuit.cg.backendtechassessment.repository.BidRepository;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
public class BidService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AutoBidRepository autobidRepository;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModelConverter modelConverter;

    public AutoBidDTO submitAutoBid(AutoBidDTO autobidDTO) {
        logger.debug("submitAutoBid: {}", autobidDTO);
        AutoBid autoBid = modelConverter.toAutoBid(autobidDTO);
        Long buyerId = autobidDTO.getBuyerId();
        Long projectId = autobidDTO.getProjectId();

        Project project = projectService.getProjectById(projectId);
        if (!project.getDeadline().isAfter(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Bid deadline has passed");
        }
        if (autoBid.getMinimumAmount() > project.getMaximumBudget()) {
            throw new OperationNotPermittedException("Autobid minimum amount exceeds project's maximum budget");
        }

        autoBid.setBuyer(buyerService.getBuyerById(buyerId));

        // only one autobid allowed per buyer per project
        // so do an update if one exists
        List<AutoBid> bids = autobidRepository.findByProjectIdAndBuyerId(projectId, buyerId);
        if (bids.size() != 0) {
            autoBid = bids.iterator().next();
            logger.debug("updating existing autobid: {}", autoBid.getId());
            autoBid.setMinimumAmount(autobidDTO.getMinimumAmount());
        }

        autoBid = autobidRepository.save(autoBid);

        logger.debug("saved autobid: {}", autoBid.getId());

        processAnyAutoBids(project.getId());

        return modelConverter.fromAutoBid(autoBid);
    }

    public BidDTO submitBid(BidDTO bidDTO) {
        logger.debug("submitBid: {}", bidDTO);
        Bid bid = modelConverter.toBid(bidDTO);
        Long buyerId = bidDTO.getBuyerId();
        Long projectId = bidDTO.getProjectId();

        Project project = projectService.getProjectById(projectId);
        if (!project.getDeadline().isAfter(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Bid deadline has passed");
        }
        if (bid.getAmount() > project.getMaximumBudget()) {
            throw new OperationNotPermittedException("Bid exceeds project's maximum budget");
        }

        bid.setBuyer(buyerService.getBuyerById(buyerId));

        projectService.validateBidIsLower(projectId, bid);

        bid = bidRepository.save(bid);

        logger.debug("saved bid: {}", bid.getId());

        projectService.updateLowestBidIfBidIsLower(projectId, bid);

        processAnyAutoBids(project.getId());

        return modelConverter.fromBid(bid);
    }

    private void processAnyAutoBids(long projectId) {
        logger.debug("processAnyAutoBids: {}", projectId);
        Project project = projectService.getProjectById(projectId);

        Bid lowestBid = project.getLowestBid();
        Long lowestBidAmount = project.getMaximumBudget();
        if (lowestBid != null) {
            lowestBidAmount = lowestBid.getAmount();
            logger.debug("lowestBidAmount is: {}", lowestBidAmount);
        } else {
            logger.debug("no current bids; maximum budget is: {}", lowestBidAmount);
        }

        List<AutoBid> lowestAutoBids = autobidRepository.findAutoBidsWithLowerMinimums(project.getId(),
                lowestBidAmount);
        if (lowestAutoBids.size() > 0) {
            logger.debug(
                    "Found {} autobids with minimums equal or less than current bid or all autobids if no current bids",
                    lowestAutoBids.size());

            AutoBid lowestAutoBid = lowestAutoBids.get(0);
            if ((lowestBid != null) && (lowestAutoBid.getMinimumAmount() == lowestBid.getAmount())) {
                if (lowestAutoBid.getBuyer().getId() == lowestBid.getBuyer().getId()) {
                    logger.debug("Buyer with lowest autobid already has the lowest bid; buyer_id: {}", lowestBid
                            .getBuyer().getId());
                } else if (lowestAutoBid.getBidTime().isBefore(lowestBid.getBidTime())) {
                    // if lowestAutoBid was submitted before lowestBid then buyer for lowestAutoBid
                    // needs to be the lowest bid
                    logger.info(
                            "Lowest autobid and lowest bid are equal and lowest autobid was submitted earlier so that buyer now gets lowest bid; buyer_id: {}",
                            lowestAutoBid.getBuyer().getId());

                    Bid bid = new Bid(lowestBid.getAmount(), lowestAutoBid.getBuyer(), project.getId());
                    bid = bidRepository.save(bid);
                    logger.debug("saved bid: {}", bid.getId());

                    projectService.updateLowestBid(projectId, bid);

                } else {
                    logger.debug(
                            "Lowest autobid and lowest bid are equal and lowest bid was submitted earlier so lowest bid remains; buyer_id: {}",
                            lowestBid.getBuyer().getId());

                }
                return;
            }
            Long newBidAmount;
            Buyer buyer = lowestAutoBid.getBuyer();
            if (lowestAutoBids.size() > 1) {
                Long secondLowestMinimumAmount = lowestAutoBids.get(1).getMinimumAmount();
                if (lowestAutoBid.getMinimumAmount() != secondLowestMinimumAmount) {
                    newBidAmount = secondLowestMinimumAmount - 1;
                    logger.info("Auto bidding one less than the second lowest autobid minimum; newBidAmount: {}",
                            newBidAmount);
                } else {
                    newBidAmount = lowestAutoBid.getMinimumAmount();
                    logger.info(
                            "At least two lowest autobid minimums are equal, so auto bidding the earliest submitted; newBidAmount: {}",
                            newBidAmount);
                }
            } else if (lowestBid != null) {
                if (buyer.getId() != lowestBid.getBuyer().getId()) {
                    newBidAmount = lowestBid.getAmount() - 1;
                    logger.info("Auto bidding one less than the currently lowest bid; newBidAmount: {}", newBidAmount);
                } else {
                    logger.debug("Only one autobid and that buyer already has the lowest bid; buyer_id: {}", buyer
                            .getId());
                    return;
                }
            } else {
                // bid the current maximum amount
                newBidAmount = project.getMaximumBudget();
                logger.info("Auto bidding the current maximum budget: {}", newBidAmount);
            }

            // submit autobid
            BidDTO newBidDTO = new BidDTO();
            newBidDTO.setBuyerId(buyer.getId());
            newBidDTO.setProjectId(project.getId());
            newBidDTO.setAmount(newBidAmount);
            logger.info("Submitting bid: {}", newBidDTO);
            submitBid(newBidDTO);
        } else {
            logger.debug("No autobids found");
        }

    }

}
