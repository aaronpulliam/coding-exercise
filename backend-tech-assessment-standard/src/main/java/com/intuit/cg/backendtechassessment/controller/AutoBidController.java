package com.intuit.cg.backendtechassessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.AutoBidDTO;
import com.intuit.cg.backendtechassessment.service.BidService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(RequestMappings.AUTOBIDS)
public class AutoBidController {

    private BidService bidService;

    public AutoBidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Submit an autobid for a project")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "AutoBid accepted"), @ApiResponse(code = 422,
            message = "AutoBid not accepted. Response body contains message with further details") })
    public AutoBidDTO submitBid(@RequestBody @ApiParam("bid details") AutoBidDTO autobid) {
        return bidService.submitAutoBid(autobid);
    }

}
