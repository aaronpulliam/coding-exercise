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
import com.intuit.cg.backendtechassessment.dto.SellerDTO;
import com.intuit.cg.backendtechassessment.service.SellerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(RequestMappings.SELLERS)
public class SellerController {

    private SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Register a seller")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Seller registered"), @ApiResponse(code = 422,
            message = "Seller could not be registered. Response body contains message with further details") })
    public SellerDTO registerSeller(@RequestBody @ApiParam("seller's information") SellerDTO seller) {
        return sellerService.createSeller(seller);
    }

    @GetMapping(value = "/{sellerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Retrieve a seller's information")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Seller's information returned"), @ApiResponse(
            code = 404, message = "Seller with that id does not exist") })
    public SellerDTO getSellectById(@PathVariable @ApiParam("seller id") long sellerId) {
        return sellerService.getSellerDTOById(sellerId);
    }

}
