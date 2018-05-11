package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.model.Seller;
import com.intuit.cg.backendtechassessment.repository.SellerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
@Validated
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ModelConverter modelConverter;

    public SellerDTO createSeller(SellerDTO sellerDTO) {
        return modelConverter.fromSeller(sellerRepository.save(modelConverter.toSeller(sellerDTO)));
    }

    Seller getSellerById(long id) {
        return sellerRepository.findById(id).orElseThrow(() -> new OperationNotPermittedException("Seller not found"));
    }

    public SellerDTO getSellerDTOById(long id) {
        return modelConverter.fromSeller(sellerRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Seller not found")));
    }

}
