package com.intuit.cg.backendtechassessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.ProjectDTO;
import com.intuit.cg.backendtechassessment.service.ProjectService;

@RestController
@RequestMapping(RequestMappings.PROJECTS)
public class ProjectController {

    private ProjectService projectService;
    private ModelConverter modelConverter;

    public ProjectController(ProjectService projectService, ModelConverter modelConverter) {
        this.projectService = projectService;
        this.modelConverter = modelConverter;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDTO createProject(@RequestBody ProjectDTO projectDTO) {
        return modelConverter.fromProject(projectService.createProject(modelConverter.toProject(projectDTO)));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProjectDTO getProjectById(@PathVariable long id) {
        return modelConverter.fromProject(projectService.getProjectById(id));
    }

}
