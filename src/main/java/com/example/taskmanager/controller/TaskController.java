package com.example.taskmanager.controller;

import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Task> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        return taskService.getAll(status, priority);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> create(@RequestBody Task task) {
        Task created = taskService.addTask(task.getTitle(), task.getDescription(), task.getPriority());
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task task) {
        return taskService.update(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Status status = Status.valueOf(statusStr.toUpperCase());
            return taskService.updateStatus(id, status)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (taskService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Map<String, Long> getStats() {
        return taskService.getStatsMap();
    }
}