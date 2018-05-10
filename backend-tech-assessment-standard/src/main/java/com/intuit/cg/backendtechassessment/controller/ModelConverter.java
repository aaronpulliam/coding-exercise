package com.intuit.cg.backendtechassessment.controller;

import org.modelmapper.ModelMapper;

import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.model.Seller;

public class ModelConverter {

    private ModelMapper modelMapper;

    public ModelConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
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

}
