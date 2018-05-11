package com.intuit.cg.backendtechassessment.dto;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.swagger.annotations.ApiModelProperty;

public class SellerDTO {
    @ApiModelProperty(notes = "Seller identifier (populated by database)")
    private Long id;

    @ApiModelProperty(notes = "Seller's first name")
    @NotBlank(message = "Seller's firstName cannot be blank")
    private String firstName;

    @ApiModelProperty(notes = "Seller's last name")
    @NotBlank(message = "Seller's lastName cannot be blank")
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
