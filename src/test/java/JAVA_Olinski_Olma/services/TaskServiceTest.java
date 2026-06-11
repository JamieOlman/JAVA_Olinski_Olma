package JAVA_Olinski_Olma.services;

import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.model.Task;
import JAVA_Olinski_Olma.model.TaskType;
import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllTasks() {
        Task t1 = new Task(); t1.setTitle("T1");
        Task t2 = new Task(); t2.setTitle("T2");
        List<Task> list = Arrays.asList(t1, t2);

        when(taskRepository.findAll()).thenReturn(list);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateTask() {
        Task t = new Task();
        t.setTitle("New");
        when(taskRepository.save(any(Task.class))).thenReturn(t);

        Task result = taskService.createTask(t);

        assertNotNull(result);
        assertEquals("New", result.getTitle());
        verify(taskRepository, times(1)).save(t);
    }

    @Test
    void shouldGetTaskById_present() {
        Task t = new Task(); t.setTitle("X");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(t));

        Optional<Task> res = taskService.getTaskById(1L);

        assertTrue(res.isPresent());
        assertEquals("X", res.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldGetTaskById_empty() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> res = taskService.getTaskById(1L);

        assertFalse(res.isPresent());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateTaskWhenExists() {
        Task existing = new Task();
        existing.setTitle("old");
        existing.setDescription("d");
        existing.setTaskType(TaskType.TASK);

        Project proj = new Project(); proj.setName("P");
        Users user = new Users(); user.setUsername("u");

        Task details = new Task();
        details.setTitle("new");
        details.setDescription("dd");
        details.setTaskType(TaskType.FEATURE);
        details.setProject(proj);
        details.setUser(user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Task> res = taskService.updateTask(1L, details);

        assertTrue(res.isPresent());
        assertEquals("new", res.get().getTitle());
        assertEquals("dd", res.get().getDescription());
        assertEquals(TaskType.FEATURE, res.get().getTaskType());
        assertEquals("P", res.get().getProject().getName());
        assertEquals("u", res.get().getUser().getUsername());
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void shouldNotUpdateTaskWhenNotExists() {
        Task details = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> res = taskService.updateTask(1L, details);

        assertFalse(res.isPresent());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskWhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean res = taskService.deleteTask(1L);

        assertTrue(res);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteTaskWhenNotExists() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean res = taskService.deleteTask(1L);

        assertFalse(res);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
