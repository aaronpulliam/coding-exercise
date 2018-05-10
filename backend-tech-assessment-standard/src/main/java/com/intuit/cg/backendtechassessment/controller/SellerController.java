package com.intuit.cg.backendtechassessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.service.SellerService;

@RestController
@RequestMapping(RequestMappings.SELLERS)
public class SellerController {

    private SellerService sellerService;
    private ModelConverter modelConverter;

    public SellerController(SellerService sellerService, ModelConverter modelConverter) {
        this.sellerService = sellerService;
        this.modelConverter = modelConverter;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SellerDTO registerSeller(@RequestBody SellerDTO sellerDTO) {
        return modelConverter.fromSeller(sellerService.createSeller(modelConverter.toSeller(sellerDTO)));
    }

}
