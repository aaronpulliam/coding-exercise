package com.intuit.cg.backendtechassessment.service;

import java.time.OffsetDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.cg.backendtechassessment.dto.ProjectDTO;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.ProjectRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private ModelConverter modelConverter;

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = modelConverter.toProject(projectDTO);
        if (project.getMaximumBudget() <= 0) {
            throw new OperationNotPermittedException("Maximum budget must be greater than zero");
        }
        if (project.getDeadline().isBefore(OffsetDateTime.now())) {
            throw new OperationNotPermittedException("Deadline cannot be in the past");
        }
        Long sellerId = projectDTO.getSellerId();
        if (sellerId == null) {
            throw new OperationNotPermittedException("Project must have a seller");
        }
        project.setSeller(sellerService.getSellerById(sellerId));

        return modelConverter.fromProject(projectRepository.save(project));
    }

    Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    public ProjectDTO getProjectDTOById(long id) {
        return modelConverter.fromProject(getProjectById(id));
    }

    public void validateBidIsLower(long projectId, Bid bid) {
        Project project = getProjectById(projectId);
        if ((project.getLowestBid() != null) && (project.getLowestBid().getAmount() <= bid.getAmount())) {
            throw new OperationNotPermittedException("Bid must be lower than " + project.getLowestBid().getAmount());
        }
    }

    public void updateLowestBidIfBidIsLower(long projectId, Bid bid) {
        validateBidIsLower(projectId, bid);

        Project project = getProjectById(projectId);
        project.setLowestBid(bid);
        projectRepository.save(project);
    }

}
