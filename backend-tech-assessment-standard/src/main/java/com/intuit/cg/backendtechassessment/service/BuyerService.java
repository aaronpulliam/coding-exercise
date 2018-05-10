package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.repository.BuyerRepository;

@Service
@Transactional
public class BuyerService {

    private BuyerRepository buyerRepository;

    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    public Buyer createBuyer(Buyer buyer) {
        return buyerRepository.save(buyer);
    }

}
