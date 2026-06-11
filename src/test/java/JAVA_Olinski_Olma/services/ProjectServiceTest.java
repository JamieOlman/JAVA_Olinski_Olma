package JAVA_Olinski_Olma.services;

import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllProjects() {
        List<Project> projects = new ArrayList<>();
        Project p1 = new Project(); p1.setName("P1");
        Project p2 = new Project(); p2.setName("P2");
        projects.add(p1); projects.add(p2);

        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateProject() {
        Project project = new Project();
        project.setName("NewProject");
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.createProject(project);

        assertNotNull(result);
        assertEquals("NewProject", result.getName());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void shouldGetProjectById_present() {
        Project project = new Project();
        project.setName("X");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectService.getProjectById(1L);

        assertTrue(result.isPresent());
        assertEquals("X", result.get().getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void shouldGetProjectById_empty() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProjectById(1L);

        assertFalse(result.isPresent());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateProjectWhenExists_andUsersNotNull() {
        Project existing = new Project();
        existing.setName("old");
        existing.setUsers(new HashSet<>());

        Project details = new Project();
        details.setName("new");
        details.setUsers(new HashSet<Users>());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Project> result = projectService.updateProject(1L, details);

        assertTrue(result.isPresent());
        assertEquals("new", result.get().getName());
        assertNotNull(result.get().getUsers());
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    void shouldUpdateProjectWhenExists_andUsersNull_preserveExisting() {
        Project existing = new Project();
        existing.setName("old");
        Set<Users> pre = new HashSet<>();
        Users u = new Users(); u.setUsername("u1");
        pre.add(u);
        existing.setUsers(pre);

        Project details = new Project();
        details.setName("new");
        details.setUsers(null);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Project> result = projectService.updateProject(1L, details);

        assertTrue(result.isPresent());
        assertEquals("new", result.get().getName());
        assertNotNull(result.get().getUsers());
        assertEquals(1, result.get().getUsers().size());
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    void shouldNotUpdateProjectWhenNotExists() {
        Project details = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.updateProject(1L, details);

        assertFalse(result.isPresent());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void shouldDeleteProjectWhenExists() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        boolean result = projectService.deleteProject(1L);

        assertTrue(result);
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteProjectWhenNotExists() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        boolean result = projectService.deleteProject(1L);

        assertFalse(result);
        verify(projectRepository, never()).deleteById(anyLong());
    }
}
