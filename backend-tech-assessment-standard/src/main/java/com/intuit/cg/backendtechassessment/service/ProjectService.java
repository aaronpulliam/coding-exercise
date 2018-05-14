package com.intuit.cg.backendtechassessment.service;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.intuit.cg.backendtechassessment.dto.ProjectDTO;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.repository.ProjectRepository;
import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@Service
@Transactional
@Validated
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private ModelConverter modelConverter;

    public ProjectDTO createProject(@Valid ProjectDTO projectDTO) {
        Project project = modelConverter.toProject(projectDTO);
        Long sellerId = projectDTO.getSellerId();
        project.setSeller(sellerService.getSellerById(sellerId));

        return modelConverter.fromProject(projectRepository.save(project));
    }

    public ProjectDTO getProjectDTOById(long id) {
        return modelConverter.fromProject(projectRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Project not found")));
    }

    public void checkThatBidIsLowerThanAnyExistingBid(long projectId, Long bidAmount) {
        Project project = getProjectById(projectId);
        if ((project.getLowestBid() != null) && (project.getLowestBid().getAmount() <= bidAmount)) {
            throw new OperationNotPermittedException("Bid must be lower than " + project.getLowestBid().getAmount());
        }
    }

    public void updateLowestBidIfBidIsLower(Bid bid) {
        checkThatBidIsLowerThanAnyExistingBid(bid.getProjectId(), bid.getAmount());
        updateLowestBid(bid);
    }

    Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(() -> new OperationNotPermittedException(
                "Project not found"));
    }

    void updateLowestBid(Bid bid) {
        Project project = getProjectById(bid.getProjectId());
        project.setLowestBid(bid);
        projectRepository.save(project);
    }

}
