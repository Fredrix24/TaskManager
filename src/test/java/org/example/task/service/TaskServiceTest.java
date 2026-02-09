package org.example.task.service;

import org.example.task.dto.TaskRequest;
import org.example.task.exception.TaskNotFoundException;
import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.example.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private UUID testId = UUID.randomUUID();
    private final TaskRequest validRequest = new TaskRequest(
            "Test Title", "Test Desc", TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(5));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Unit тесты
    //Тест 1: Успешное создание
    @Test
    void shouldCreateTaskSuccessfully() {
        Task mockSavedTask = new Task(testId, "Test Title", "Test Desc", TaskStatus.IN_PROGRESS, LocalDateTime.now(), validRequest.dueDate());
        when(taskRepository.save(any(Task.class))).thenReturn(mockSavedTask);

        Task result = taskService.create(validRequest);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    //Тест 2: Обновление не найденной задачи вызывает исключение
    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistentTask() {
        when(taskRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.update(testId, validRequest);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    //Тест 3: Успешное удаление
    @Test
    void shouldDeleteTaskSuccessfully() {
        when(taskRepository.findById(testId)).thenReturn(Optional.of(mock(Task.class)));

        taskService.delete(testId);

        verify(taskRepository, times(1)).deleteById(testId);
    }
}