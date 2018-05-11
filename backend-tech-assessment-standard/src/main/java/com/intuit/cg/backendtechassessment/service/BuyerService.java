package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.repository.BuyerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;

@Service
@Transactional
public class BuyerService {

    private BuyerRepository buyerRepository;
    private ModelConverter modelConverter;

    public BuyerService(BuyerRepository buyerRepository, ModelConverter modelConverter) {
        this.buyerRepository = buyerRepository;
        this.modelConverter = modelConverter;
    }

    public BuyerDTO createBuyer(BuyerDTO buyerDTO) {
        return modelConverter.fromBuyer(buyerRepository.save(modelConverter.toBuyer(buyerDTO)));
    }

    public BuyerDTO getBuyerById(long id) {
        return modelConverter.fromBuyer(buyerRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Buyer not found")));
    }

}
