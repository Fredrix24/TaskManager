package org.example.task.service;

import org.example.task.dto.TaskRequest;
import org.example.task.exception.TaskNotFoundException;
import org.example.task.model.Task;
import org.example.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    //Вывод всех задач
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    //Создание задачи
    public Task create(TaskRequest request) {
        Task newTask = new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.status(),
                LocalDateTime.now(),
                request.dueDate()
        );
        return taskRepository.save(newTask);
    }
    //Обновление задачи
    public Task update(UUID id, TaskRequest request) throws TaskNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        Task updatedTask = new Task(
                existingTask.id(),
                request.title(),
                request.description(),
                request.status(),
                existingTask.createdAt(),
                request.dueDate()
        );
        return taskRepository.save(updatedTask);
    }
    //Удаление задачи
    public void delete(UUID id) throws TaskNotFoundException {
        if (!taskRepository.findById(id).isPresent()) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
    //Поиск задачи по ID
    public Optional<Task> findById(UUID id) {
        return taskRepository.findById(id);
    }
}