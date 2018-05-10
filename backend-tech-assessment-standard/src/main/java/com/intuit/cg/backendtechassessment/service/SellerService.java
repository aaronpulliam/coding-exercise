package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.model.Seller;
import com.intuit.cg.backendtechassessment.repository.SellerRepository;

@Service
@Transactional
public class SellerService {

    private SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller createSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

}
