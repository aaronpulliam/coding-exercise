package com.intuit.cg.backendtechassessment.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public class BuyerDTO {
    @ApiModelProperty(notes = "Buyer identifier (populated by database)")
    private Long id;

    @ApiModelProperty(notes = "Buyer's first name")
    @NotBlank(message = "Buyer's firstName cannot be blank")
    private String firstName;

    @ApiModelProperty(notes = "Buyer's last name")
    @NotBlank(message = "Buyer's lastName cannot be blank")
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

}
