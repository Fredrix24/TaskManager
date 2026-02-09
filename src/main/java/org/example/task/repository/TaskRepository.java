package org.example.task.repository;

import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TaskRepository {

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    //Инициализация тестовыми данными
    public void initializeData() {
        if (tasks.isEmpty()) {
            Task task1 = new Task(
                    UUID.randomUUID(),
                    "Setup Project",
                    "Configure basic Spring Boot structure.",
                    TaskStatus.DONE,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().minusHours(1)
            );
            tasks.put(task1.id(), task1);
        }
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task save(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    public void deleteById(UUID id) {
        tasks.remove(id);
    }
}