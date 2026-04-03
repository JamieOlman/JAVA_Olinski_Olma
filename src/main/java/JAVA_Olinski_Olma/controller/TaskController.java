package JAVA_Olinski_Olma.controller;

import JAVA_Olinski_Olma.model.Task;
import JAVA_Olinski_Olma.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
private final TaskRepository taskRepository;

public TaskController(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
}

@GetMapping
public List<Task> getAllTasks() {
    return taskRepository.findAll();
}

@PostMapping
public Task createTask(@RequestBody Task task) {
    return taskRepository.save(task);
}
}