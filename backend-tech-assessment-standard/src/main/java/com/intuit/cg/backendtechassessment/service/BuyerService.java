package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.model.Buyer;
import com.intuit.cg.backendtechassessment.repository.BuyerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
@Validated
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ModelConverter modelConverter;

    public BuyerDTO createBuyer(@Valid BuyerDTO buyerDTO) {
        return modelConverter.fromBuyer(buyerRepository.save(modelConverter.toBuyer(buyerDTO)));
    }

    public BuyerDTO getBuyerDTOById(long id) {
        return modelConverter.fromBuyer(buyerRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Buyer not found")));
    }

    Buyer getBuyerById(long id) {
        return buyerRepository.findById(id).orElseThrow(() -> new OperationNotPermittedException("Buyer not found"));
    }

}
