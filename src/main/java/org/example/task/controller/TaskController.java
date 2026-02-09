package org.example.task.controller;

import org.example.task.dto.TaskRequest;
import org.example.task.exception.TaskNotFoundException;
import org.example.task.model.Task;
import org.example.task.repository.TaskRepository;
import org.example.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;

        taskRepository.initializeData();
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @Valid @RequestBody TaskRequest request) {
        Task created = taskService.create(request);
        return ResponseEntity
                .created(URI.create("/tasks/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request) {
        try {
            Task updated = taskService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (TaskNotFoundException e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        try {
            taskService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            throw e;
        }
    }
}