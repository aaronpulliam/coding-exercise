package com.intuit.cg.backendtechassessment.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Long id;

    @Version
    private int version;

    @Column(name = "firstName", nullable = false)
    private String description;

    @Column(name = "maximumBudget", nullable = false)
    private Long maximumBudget;

    @Column(name = "deadline", nullable = false)
    private OffsetDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    protected Project() {

    }

    public Project(String description, Long maximumBudget, OffsetDateTime deadline) {
        this.description = description;
        this.maximumBudget = maximumBudget;
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

}
