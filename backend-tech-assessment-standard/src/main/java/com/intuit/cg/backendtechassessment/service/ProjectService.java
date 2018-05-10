package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.ProjectRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
        if (project.getMaximumBudget() <= 0) {
            throw new OperationNotPermittedException("Maximum budget must be greater than zero");
        }
        if (project.getDeadline().isBefore(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Deadline cannot be in the past");
        }
        return projectRepository.save(project);
    }

    public Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }

}
