package com.intuit.cg.backendtechassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.cg.backendtechassessment.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
