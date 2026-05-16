package JAVA_Olinski_Olma.controller;

import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Projects", description = "Operations on projects")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Retrieve all projects", description = "Fetches a list of all projects from the database")
    @GetMapping("/all")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "Create a new project", description = "Creates a new project with the provided details")
    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing project", description = "Updates the project with the specified ID")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @Parameter(name = "id", description = "The unique identifier of the project to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Project projectDetails) {
        var updatedProject = projectService.updateProject(id, projectDetails);
        return updatedProject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a project by ID", description = "Deletes the project with the specified ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @Parameter(name = "id", description = "The unique identifier of the project to delete", required = true, example = "1")
            @PathVariable Long id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}