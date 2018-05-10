package com.intuit.cg.backendtechassessment.dto;

import java.time.OffsetDateTime;

public class ProjectDTO {

    private Long id;
    private String description;
    private Long maximumBudget;
    private OffsetDateTime deadline;
    private SellerDTO sellerDTO;

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

    public SellerDTO getSellerDTO() {
        return sellerDTO;
    }

    public void setSellerDTO(SellerDTO sellerDTO) {
        this.sellerDTO = sellerDTO;
    }

}
