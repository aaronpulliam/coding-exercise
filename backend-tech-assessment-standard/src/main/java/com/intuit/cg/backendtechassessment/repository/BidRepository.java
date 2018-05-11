package com.intuit.cg.backendtechassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.cg.backendtechassessment.model.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {

}
