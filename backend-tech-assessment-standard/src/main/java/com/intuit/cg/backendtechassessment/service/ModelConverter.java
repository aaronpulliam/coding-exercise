package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import com.intuit.cg.backendtechassessment.dto.AutoBidDTO;
import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.dto.ProjectDTO;
import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.model.AutoBid;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.model.Seller;

public class ModelConverter {

    private ModelMapper modelMapper;

    public ModelConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public BuyerDTO fromBuyer(Buyer buyer) {
        return modelMapper.map(buyer, BuyerDTO.class);
    }

    public Buyer toBuyer(BuyerDTO buyerDTO) {
        return modelMapper.map(buyerDTO, Buyer.class);
    }

    public SellerDTO fromSeller(Seller seller) {
        return modelMapper.map(seller, SellerDTO.class);
    }

    public Seller toSeller(SellerDTO sellerDTO) {
        return modelMapper.map(sellerDTO, Seller.class);
    }

    public ProjectDTO fromProject(Project project) {
        ProjectDTO projectDTO = modelMapper.map(project, ProjectDTO.class);
        projectDTO.setSellerId(project.getSeller().getId());
        Bid lowestBid = project.getLowestBid();
        if (lowestBid != null) {
            projectDTO.setLowestBidAmount(lowestBid.getAmount());
            if (!project.getDeadline().isAfter(OffsetDateTime.now())) {
                projectDTO.setWinningBidderId(lowestBid.getBuyer().getId());
            }
        }
        return projectDTO;
    }

    public Project toProject(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }

    public Bid toBid(BidDTO bidDTO) {
        return modelMapper.map(bidDTO, Bid.class);
    }

    public BidDTO fromBid(Bid bid) {
        BidDTO bidDTO = modelMapper.map(bid, BidDTO.class);
        bidDTO.setBuyerId(bid.getBuyer().getId());
        return bidDTO;
    }

    public AutoBid toAutoBid(AutoBidDTO autoBidDTO) {
        return modelMapper.map(autoBidDTO, AutoBid.class);
    }

    public AutoBidDTO fromAutoBid(AutoBid bid) {
        AutoBidDTO autoBidDTO = modelMapper.map(bid, AutoBidDTO.class);
        autoBidDTO.setBuyerId(bid.getBuyer().getId());
        return autoBidDTO;
    }

}
