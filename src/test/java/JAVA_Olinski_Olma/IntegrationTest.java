package JAVA_Olinski_Olma;

import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.model.Task;
import JAVA_Olinski_Olma.model.TaskType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class IntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testProjectFlow() throws Exception {
        Project project = new Project();
        project.setName("Projekt CRM");
        project.setUsers(new HashSet<>());

        String createResponse = mockMvc.perform(post("/api/projects/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Projekt CRM"))
                .andReturn().getResponse().getContentAsString();

        Project createdProject = objectMapper.readValue(createResponse, Project.class);


        mockMvc.perform(get("/api/projects/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/projects/" + createdProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projekt CRM"));

        mockMvc.perform(get("/api/projects/9999"))
                .andExpect(status().isNotFound());

        Project projectDetails = new Project();
        projectDetails.setName("Zaktualizowany Projekt CRM");
        projectDetails.setUsers(null);

        mockMvc.perform(put("/api/projects/" + createdProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zaktualizowany Projekt CRM"));

        Project projectDetailsWithUsers = new Project();
        projectDetailsWithUsers.setName("Zaktualizowany Projekt CRM 2");
        projectDetailsWithUsers.setUsers(new HashSet<>());

        mockMvc.perform(put("/api/projects/" + createdProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDetailsWithUsers)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/projects/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDetails)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/projects/" + createdProject.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/projects/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testTaskAndUserFlow() throws Exception {
        Users user = new Users();
        user.setUsername("Jan Kowalski");
        user.setProjects(new HashSet<>());

        String createUserResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jan Kowalski"))
                .andReturn().getResponse().getContentAsString();

        Users createdUser = objectMapper.readValue(createUserResponse, Users.class);


        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/users/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jan Kowalski"));

        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());


        Users userDetails = new Users();
        userDetails.setUsername("Anna Nowak");
        userDetails.setProjects(null);

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Anna Nowak"));

        Users userDetailsWithProjects = new Users();
        userDetailsWithProjects.setUsername("Anna Nowak 2");
        userDetailsWithProjects.setProjects(new HashSet<>());

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsWithProjects)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isNotFound());

        Project project = new Project();
        project.setName("Projekt Alfa");
        project.setUsers(new HashSet<>());

        String createProjectResponse = mockMvc.perform(post("/api/projects/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Project createdProject = objectMapper.readValue(createProjectResponse, Project.class);

        Task task = new Task();
        task.setTitle("Zadanie 1");
        task.setDescription("Opis zadania 1");
        task.setTaskType(TaskType.BUG);
        task.setProject(createdProject);
        task.setUser(createdUser);

        String createTaskResponse = mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Zadanie 1"))
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(createTaskResponse, Task.class);


        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/tasks/" + createdTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Zadanie 1"));

        mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound());

        Task taskDetails = new Task();
        taskDetails.setTitle("Zadanie 1 Zmienione");
        taskDetails.setDescription("Nowy opis");
        taskDetails.setTaskType(TaskType.FEATURE);
        taskDetails.setProject(createdProject);
        taskDetails.setUser(createdUser);

        mockMvc.perform(put("/api/tasks/" + createdTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Zadanie 1 Zmienione"));

        mockMvc.perform(put("/api/tasks/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDetails)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tasks/" + createdTask.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/tasks/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/users/" + createdUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNotFound());
    }
}