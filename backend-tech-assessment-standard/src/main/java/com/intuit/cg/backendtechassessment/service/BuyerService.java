package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.repository.BuyerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;

@Service
@Transactional
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ModelConverter modelConverter;

    public BuyerDTO createBuyer(BuyerDTO buyerDTO) {
        return modelConverter.fromBuyer(buyerRepository.save(modelConverter.toBuyer(buyerDTO)));
    }

    public BuyerDTO getBuyerDTOById(long id) {
        return modelConverter.fromBuyer(getBuyerById(id));
    }

    Buyer getBuyerById(long id) {
        return buyerRepository.findById(id).orElseThrow(() -> new NotFoundException("Buyer not found"));
    }

}
