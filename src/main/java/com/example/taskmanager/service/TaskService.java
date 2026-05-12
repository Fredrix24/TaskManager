package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    Task addTask(String title, String description, Priority priority);
    Task addTask(String title, String description);
    List<Task> getAllTasks();
    List<Task> getAll(String status, String priority);
    Optional<Task> findById(Long id);
    Optional<Task> update(Long id, Task task);
    Optional<Task> updateStatus(Long id, Status status);
    void delete(Long id);
    long getTaskCount();
    String getStats();
    Map<String, Long> getStatsMap();
}