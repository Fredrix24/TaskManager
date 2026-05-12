package com.example.taskmanager.service;

import com.example.taskmanager.config.AppProperties;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final AppProperties appProperties;

    public TaskServiceImpl(TaskRepository taskRepository, AppProperties appProperties) {
        this.taskRepository = taskRepository;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        System.out.println("TaskService инициализирован");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Завершение работы. Задач в хранилище: " + taskRepository.count());
    }

    @Override
    public Task addTask(String title, String description, Priority priority) {
        if (taskRepository.count() >= appProperties.getMaxTasks()) {
            throw new IllegalStateException("Превышен лимит задач! Максимум: " + appProperties.getMaxTasks());
        }
        Priority p = (priority != null) ? priority : appProperties.getDefaultPriority();
        Task task = new Task(title, description, p);
        return taskRepository.save(task);
    }

    @Override
    public Task addTask(String title, String description) {
        return addTask(title, description, null);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getAll(String status, String priority) {
        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(
                    Status.valueOf(status.toUpperCase()),
                    Priority.valueOf(priority.toUpperCase()));
        }
        if (status != null) {
            return taskRepository.findByStatus(Status.valueOf(status.toUpperCase()));
        }
        if (priority != null) {
            return taskRepository.findByPriority(Priority.valueOf(priority.toUpperCase()));
        }
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Optional<Task> update(Long id, Task task) {
        Optional<Task> existing = taskRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Task t = existing.get();
        if (task.getTitle() != null) t.setTitle(task.getTitle());
        if (task.getDescription() != null) t.setDescription(task.getDescription());
        if (task.getPriority() != null) t.setPriority(task.getPriority());
        if (task.getStatus() != null) t.setStatus(task.getStatus());
        return Optional.of(taskRepository.save(t));
    }

    @Override
    public Optional<Task> updateStatus(Long id, Status status) {
        Optional<Task> existing = taskRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Task t = existing.get();
        t.setStatus(status);
        return Optional.of(taskRepository.save(t));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public long getTaskCount() {
        return taskRepository.count();
    }

    @Override
    public String getStats() {
        // Убираем ObjectProvider, так как TaskStatsService остался без JPA
        return "Stats: " + taskRepository.count() + " tasks total";
    }

    @Override
    public Map<String, Long> getStatsMap() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));
    }
}