package com.intuit.cg.backendtechassessment.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.swagger.annotations.ApiModelProperty;

public class AutoBidDTO {

    @ApiModelProperty(notes = "AutoBid identifier (populated by database)")
    private Long id;

    @ApiModelProperty(notes = "Minimum bid in dollars")
    @Min(value = 1, message = "Bid must be greater than zero")
    private Long minimumAmount;

    @ApiModelProperty(notes = "Identifier for the buyer associated with the autobid")
    @NotNull(message = "AutoBid must have a buyer")
    private Long buyerId;

    @ApiModelProperty(notes = "Identifier for the project associated with the autobid")
    @NotNull(message = "AutoBid must have a project")
    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Long minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
