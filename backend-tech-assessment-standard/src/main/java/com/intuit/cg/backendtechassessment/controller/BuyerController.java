package com.intuit.cg.backendtechassessment.controller;

import javax.validation.Valid;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(RequestMappings.BUYERS)
public class BuyerController {

    private BuyerService buyerService;

    public BuyerController(BuyerService buyerService, ModelConverter modelConverter) {
        this.buyerService = buyerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Register a buyer")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Buyer registered"), @ApiResponse(code = 422,
            message = "Buyer could not be registered. Response body contains message with further details") })
    public BuyerDTO registerBuyer(@RequestBody @ApiParam("buyer's information") @Valid BuyerDTO buyer) {
        return buyerService.createBuyer(buyer);
    }

    @GetMapping(value = "/{buyerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Retrieve a buyer's information")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Buyer's information returned"), @ApiResponse(code = 404,
            message = "Buyer with that id does not exist") })
    public BuyerDTO getBuyerById(@PathVariable @ApiParam("buyer id") long buyerId) {
        return buyerService.getBuyerDTOById(buyerId);
    }

}
