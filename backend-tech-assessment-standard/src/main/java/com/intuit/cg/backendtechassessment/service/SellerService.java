package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.repository.SellerRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;

@Service
@Transactional
public class SellerService {

    private SellerRepository sellerRepository;
    private ModelConverter modelConverter;

    public SellerService(SellerRepository sellerRepository, ModelConverter modelConverter) {
        this.sellerRepository = sellerRepository;
        this.modelConverter = modelConverter;
    }

    public SellerDTO createSeller(SellerDTO sellerDTO) {
        return modelConverter.fromSeller(sellerRepository.save(modelConverter.toSeller(sellerDTO)));
    }

    public SellerDTO getSellerById(long id) {
        return modelConverter.fromSeller(sellerRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Seller not found")));
    }

}
