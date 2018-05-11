package com.intuit.cg.backendtechassessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.service.BuyerService;
import com.intuit.cg.backendtechassessment.service.ModelConverter;

@RestController
@RequestMapping(RequestMappings.BUYERS)
public class BuyerController {

    private BuyerService buyerService;

    public BuyerController(BuyerService buyerService, ModelConverter modelConverter) {
        this.buyerService = buyerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BuyerDTO registerBuyer(@RequestBody BuyerDTO buyerDTO) {
        return buyerService.createBuyer(buyerDTO);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BuyerDTO getBuyerById(@PathVariable long id) {
        return buyerService.getBuyerById(id);
    }

}
