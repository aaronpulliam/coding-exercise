package com.intuit.cg.backendtechassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.cg.backendtechassessment.model.Buyer;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

}
