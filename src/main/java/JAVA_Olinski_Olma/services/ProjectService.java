package JAVA_Olinski_Olma.services;
import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> updateProject(Long id, Project projectDetails) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            Project existingProject = project.get();
            existingProject.setName(projectDetails.getName());
            if (projectDetails.getUsers() != null) {
                existingProject.setUsers(projectDetails.getUsers());
            }
            return Optional.of(projectRepository.save(existingProject));
        }
        return Optional.empty();
    }

    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
}