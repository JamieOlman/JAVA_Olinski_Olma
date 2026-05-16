package JAVA_Olinski_Olma.controller;

import JAVA_Olinski_Olma.model.Task;
import JAVA_Olinski_Olma.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Tasks", description = "Operations on tasks")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Retrieve all tasks", description = "Fetches a list of all tasks from the database")
    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing task", description = "Updates the task with the specified ID")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(name = "id", description = "The unique identifier of the task to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Task taskDetails) {
        var updatedTask = taskService.updateTask(id, taskDetails);
        return updatedTask.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a task by ID", description = "Deletes the task with the specified ID from the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(name = "id", description = "The unique identifier of the task to delete", required = true, example = "1")
            @PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}