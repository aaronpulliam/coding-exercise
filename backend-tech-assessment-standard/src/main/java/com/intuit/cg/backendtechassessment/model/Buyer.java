package com.intuit.cg.backendtechassessment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "buyer")
public class Buyer extends User {

    @Id
    @GeneratedValue
    @Column(name = "buyer_id")
    private Long id;

    protected Buyer() {
    }

    public Buyer(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
