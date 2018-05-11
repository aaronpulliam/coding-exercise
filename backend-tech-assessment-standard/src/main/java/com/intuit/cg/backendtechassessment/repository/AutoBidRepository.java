package com.intuit.cg.backendtechassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.intuit.cg.backendtechassessment.model.AutoBid;

public interface AutoBidRepository extends JpaRepository<AutoBid, Long> {

    List<AutoBid> findByProjectIdAndBuyerId(Long projectId, Long buyerId);

    @Query("select a from AutoBid a where a.projectId = :projectId and a.minimumAmount <= :currentAmount ORDER BY a.minimumAmount ASC, a.bidTime ASC")
    List<AutoBid> findAutoBidsWithLowerMinimums(@Param("projectId") Long projectId,
            @Param("currentAmount") Long currentAmount);

}
