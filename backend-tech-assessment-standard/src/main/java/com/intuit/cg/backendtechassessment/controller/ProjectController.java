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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(RequestMappings.PROJECTS)
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a project")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project created"), @ApiResponse(code = 422,
            message = "Project details not acceptable. Response body contains message with further details") })
    public ProjectDTO createProject(@RequestBody @ApiParam("project information") ProjectDTO project) {
        return projectService.createProject(project);
    }

    @GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Retrieve a project's information")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Project's information returned"), @ApiResponse(
            code = 404, message = "Project with that id does not exist") })
    public ProjectDTO getProjectById(@PathVariable @ApiParam("project identifier") long projectId) {
        return projectService.getProjectDTOById(projectId);
    }

}
