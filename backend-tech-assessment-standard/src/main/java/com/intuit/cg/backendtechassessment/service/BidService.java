package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.intuit.cg.backendtechassessment.dto.AutoBidDTO;
import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.model.AutoBid;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.AutoBidRepository;
import com.intuit.cg.backendtechassessment.repository.BidRepository;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
@Validated
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

    public BidDTO submitBid(@Valid BidDTO bidDTO) {
        logger.debug("submitBid: {}", bidDTO);

        Long projectId = bidDTO.getProjectId();
        Long bidAmount = bidDTO.getAmount();

        checkBidAgainstProjectConstraints(projectId, bidAmount);

        projectService.checkThatBidIsLowerThanAnyExistingBid(projectId, bidAmount);

        Bid bid = modelConverter.toBid(bidDTO);
        bid.setBuyer(buyerService.getBuyerById(bidDTO.getBuyerId()));

        bid = bidRepository.save(bid);

        logger.debug("saved bid: bid_id {}", bid.getId());

        projectService.updateLowestBidIfBidIsLower(bid);

        processAnyAutoBids(projectId);

        return modelConverter.fromBid(bid);
    }

    public AutoBidDTO submitAutoBid(@Valid AutoBidDTO autobidDTO) {
        logger.debug("submitAutoBid: {}", autobidDTO);
        Long projectId = autobidDTO.getProjectId();

        checkBidAgainstProjectConstraints(projectId, autobidDTO.getMinimumAmount());

        AutoBid autoBid = getAutoBidForInsertOrUpdate(autobidDTO);

        autoBid = autobidRepository.save(autoBid);

        logger.debug("saved autobid; autobid_id {}", autoBid.getId());

        processAnyAutoBids(projectId);

        return modelConverter.fromAutoBid(autoBid);
    }

    private AutoBid getAutoBidForInsertOrUpdate(AutoBidDTO autobidDTO) {
        AutoBid autoBid = getBuyersAutoBidForProjectIfAny(autobidDTO);
        if (autoBid == null) {
            // no existing AutoBid
            autoBid = modelConverter.toAutoBid(autobidDTO);
            autoBid.setBuyer(buyerService.getBuyerById(autobidDTO.getBuyerId()));
        }
        return autoBid;
    }

    private AutoBid getBuyersAutoBidForProjectIfAny(AutoBidDTO autobidDTO) {
        // only one autobid allowed per buyer per project
        // so if one exists we need to do an update
        AutoBid autoBid = null;
        List<AutoBid> bids = autobidRepository.findByProjectIdAndBuyerId(autobidDTO.getProjectId(), autobidDTO
                .getBuyerId());
        if (bids.size() > 0) {
            autoBid = bids.iterator().next();
            logger.debug("updating existing autobid; autobid_id: {}", autoBid.getId());
            autoBid.setMinimumAmount(autobidDTO.getMinimumAmount());
        }
        return autoBid;
    }

    private void checkBidAgainstProjectConstraints(Long projectId, Long bidAmount) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getDeadline().isAfter(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Bid deadline has passed");
        }
        if (bidAmount > project.getMaximumBudget()) {
            throw new OperationNotPermittedException("Bid exceeds project's maximum budget");
        }
    }

    private void processAnyAutoBids(long projectId) {
        logger.debug("processAnyAutoBids; projectId: {}", projectId);
        Project project = projectService.getProjectById(projectId);

        Bid currentLowestBidForProject = project.getLowestBid();
        List<AutoBid> lowestAutoBids = findAutoBidsToConsider(project);

        if (lowestAutoBids.size() > 0) {
            AutoBid lowestAutoBid = lowestAutoBids.get(0);
            if (currentLowestBidAmountEqualsLowestAutoBidAmount(currentLowestBidForProject, lowestAutoBid)) {
                handleCurrentLowestBidAmountEqualsLowestAutoBidAmount(currentLowestBidForProject, lowestAutoBid);
            } else if ((lowestAutoBids.size() == 1) && autoBidBuyerAlreadyHasLowestBid(lowestAutoBid,
                    currentLowestBidForProject)) {
                // do nothing
                logger.debug("Only one autobid and that buyer already has the lowest bid; buyer_id: {}", lowestAutoBid
                        .getBuyer().getId());
            } else {
                // make a new bid for lowest AutoBid buyer
                makeNewBidForLowestAutoBidBuyer(project, lowestAutoBids);
            }
        } else {
            logger.debug("No AutoBids found");
        }
    }

    private void makeNewBidForLowestAutoBidBuyer(Project project, List<AutoBid> lowestAutoBids) {
        Bid currentLowestBidForProject = project.getLowestBid();

        Long newBidAmount = getBidAmountToMake(project.getMaximumBudget(), currentLowestBidForProject, lowestAutoBids);

        AutoBid lowestAutoBid = lowestAutoBids.get(0);

        BidDTO newBidDTO = new BidDTO();
        newBidDTO.setBuyerId(lowestAutoBid.getBuyer().getId());
        newBidDTO.setProjectId(project.getId());
        newBidDTO.setAmount(newBidAmount);

        logger.info("Submitting bid: {}", newBidDTO);
        submitBid(newBidDTO);
    }

    private boolean autoBidBuyerAlreadyHasLowestBid(AutoBid lowestAutoBid, Bid currentLowestBidForProject) {
        return ((currentLowestBidForProject != null) && (lowestAutoBid.getBuyer().getId() == currentLowestBidForProject
                .getBuyer().getId()));
    }

    private boolean currentLowestBidAmountEqualsLowestAutoBidAmount(Bid currentLowestBidForProject,
            AutoBid lowestAutoBid) {
        return (currentLowestBidForProject != null) && (lowestAutoBid.getMinimumAmount() == currentLowestBidForProject
                .getAmount());
    }

    private Long getBidAmountToMake(Long projectMaximumBudget, Bid currentLowestBidForProject,
            List<AutoBid> lowestAutoBids) {
        AutoBid lowestAutoBid = lowestAutoBids.get(0);
        Long newBidAmount;
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
        } else if (currentLowestBidForProject != null) {
            newBidAmount = currentLowestBidForProject.getAmount() - 1;
            logger.info("Auto bidding one less than the currently lowest bid; newBidAmount: {}", newBidAmount);
        } else {
            // bid the current maximum amount
            newBidAmount = projectMaximumBudget;
            logger.info("Auto bidding the current maximum budget: {}", newBidAmount);
        }
        return newBidAmount;
    }

    private void handleCurrentLowestBidAmountEqualsLowestAutoBidAmount(Bid currentLowestBidForProject,
            AutoBid lowestAutoBid) {
        if (lowestAutoBid.getBuyer().getId() == currentLowestBidForProject.getBuyer().getId()) {
            logger.debug("Buyer with lowest autobid already has the lowest bid; buyer_id: {}",
                    currentLowestBidForProject.getBuyer().getId());
        } else if (lowestAutoBid.getBidTime().isBefore(currentLowestBidForProject.getBidTime())) {
            // if lowestAutoBid was submitted before current lowestBid then submit bid for
            // buyer at lowest bid amount
            logger.info(
                    "Lowest autobid and lowest bid are equal and lowest autobid was submitted earlier so that buyer now gets lowest bid; buyer_id: {}",
                    lowestAutoBid.getBuyer().getId());

            Bid bid = new Bid(currentLowestBidForProject.getAmount(), lowestAutoBid.getBuyer(),
                    currentLowestBidForProject.getProjectId());
            bid = bidRepository.save(bid);
            logger.debug("saved bid; bid_id: {}", bid.getId());

            projectService.updateLowestBid(bid);
        } else {
            logger.debug(
                    "Lowest autobid and lowest bid are equal and lowest bid was submitted earlier so lowest bid remains; buyer_id: {}",
                    currentLowestBidForProject.getBuyer().getId());
        }
    }

    private List<AutoBid> findAutoBidsToConsider(Project project) {
        Bid lowestBidForProject = project.getLowestBid();
        Long lowestBidAmount = project.getMaximumBudget();
        if (lowestBidForProject != null) {
            lowestBidAmount = lowestBidForProject.getAmount();
            logger.debug("lowestBidAmount is: {}", lowestBidAmount);
        } else {
            logger.debug("no current bids; maximum budget is: {}", lowestBidAmount);
        }
        List<AutoBid> lowestAutoBids = autobidRepository.findAutoBidsWithLowerMinimums(project.getId(),
                lowestBidAmount);
        logger.debug(
                "Found {} autobids with minimums equal or less than current bid or all autobids if no current bids",
                lowestAutoBids.size());
        return lowestAutoBids;
    }

}
