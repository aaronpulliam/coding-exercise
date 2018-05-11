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
@Table(name = "autobid")
public class AutoBid {

    @Id
    @GeneratedValue
    @Column(name = "autobid_id")
    private Long id;

    @Version
    private int version;

    @Column(name = "minimum_amount", nullable = false)
    private Long minimumAmount;

    @Column(name = "bid_time", nullable = false)
    private OffsetDateTime bidTime = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    private Long projectId;

    protected AutoBid() {
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

    public Long getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Long minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public OffsetDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(OffsetDateTime bidTime) {
        this.bidTime = bidTime;
    }

}
