package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.BidRepository;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
public class BidService {

    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModelConverter modelConverter;

    public BidDTO submitBid(BidDTO bidDTO) {
        Bid bid = modelConverter.toBid(bidDTO);
        if (bid.getAmount() <= 0) {
            throw new OperationNotPermittedException("Bid must be greater than zero");
        }
        Long buyerId = bid.getBuyerId();
        if (buyerId == null) {
            throw new OperationNotPermittedException("Bid must have a buyer");
        }
        Long projectId = bid.getProjectId();
        if (projectId == null) {
            throw new OperationNotPermittedException("Bid must have a project");
        }

        Project project = projectService.getProjectById(projectId);
        if (!project.getDeadline().isAfter(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Bid deadline has passed");
        }

        bid.setBuyer(buyerService.getBuyerById(buyerId));

        projectService.validateBidIsLower(projectId, bid);

        bid = bidRepository.save(bid);

        projectService.updateLowestBidIfBidIsLower(projectId, bid);

        return modelConverter.fromBid(bid);
    }

}
