package com.intuit.cg.backendtechassessment.dto;

import java.time.OffsetDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class ProjectDTO {

    @ApiModelProperty(notes = "Project identifier (populated by database)")
    private Long id;

    @ApiModelProperty(notes = "Detailed description for the project")
    @NotBlank(message = "Project's description cannot be blank")
    private String description;

    @ApiModelProperty(notes = "Maximum allowed bid in dollars")
    @Min(value = 1, message = "Maximum budget must be greater than zero")
    private Long maximumBudget;

    @ApiModelProperty(notes = "Deadline for submitting bids")
    @Future(message = "Deadline cannot be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime deadline;

    @ApiModelProperty(notes = "Identifier for the project's seller")
    @NotNull(message = "Project must have a seller")
    private Long sellerId;

    @ApiModelProperty(notes = "Lowest bid amount for the project (if any)")
    private Long lowestBidAmount;

    @ApiModelProperty(notes = "Identifier for the winning bidder (if deadline has passed)")
    private Long winningBidderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMaximumBudget() {
        return maximumBudget;
    }

    public void setMaximumBudget(Long maximumBudget) {
        this.maximumBudget = maximumBudget;
    }

    public OffsetDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline;
    }

    public Long getLowestBidAmount() {
        return lowestBidAmount;
    }

    public void setLowestBidAmount(Long lowestBidAmount) {
        this.lowestBidAmount = lowestBidAmount;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getWinningBidderId() {
        return winningBidderId;
    }

    public void setWinningBidderId(Long winningBidderId) {
        this.winningBidderId = winningBidderId;
    }

}
