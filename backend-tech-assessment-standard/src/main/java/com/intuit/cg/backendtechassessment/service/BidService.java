package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.repository.BidRepository;
import com.intuit.cg.backendtechassessment.repository.BuyerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
public class BidService {

    private BidRepository bidRepository;
    private BuyerRepository buyerRepository;
    private ProjectService projectService;
    private ModelConverter modelConverter;

    public BidService(BidRepository bidRepository, BuyerRepository buyerRepository, ProjectService projectService,
            ModelConverter modelConverter) {
        this.bidRepository = bidRepository;
        this.buyerRepository = buyerRepository;
        this.projectService = projectService;
        this.modelConverter = modelConverter;
    }

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

        bid.setBuyer(buyerRepository.findById(buyerId).orElseThrow(() -> new NotFoundException("Buyer not found")));

        projectService.validateBidIsLower(projectId, bid);

        bid = bidRepository.save(bid);

        projectService.updateLowestBidIfBidIsLower(projectId, bid);

        return modelConverter.fromBid(bid);
    }

}
