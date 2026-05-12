package com.example.taskmanager.service;

import com.example.taskmanager.config.AppProperties;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private TaskServiceImpl taskService;

    // ==================== getAll() ====================

    @Test
    void getAll_shouldReturnAllTasksAsList() {
        Task task = buildTask(1L, "Купить молоко", Priority.HIGH, Status.NEW);
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Купить молоко");
        verify(taskRepository).findAll();
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<Task> result = taskService.getAllTasks();

        assertThat(result).isEmpty();
    }

    // ==================== findById() ====================

    @Test
    void findById_whenExists_shouldReturnTask() {
        Task task = buildTask(1L, "Купить молоко", Priority.HIGH, Status.NEW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Купить молоко");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.findById(999L);

        assertThat(result).isEmpty();
    }

    // ==================== addTask() ====================

    @Test
    void addTask_shouldSaveTaskWithStatusNew() {
        Task task = buildTask(1L, "Купить молоко", Priority.HIGH, Status.NEW);
        when(appProperties.getMaxTasks()).thenReturn(10);
        when(taskRepository.count()).thenReturn(0L);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.addTask("Купить молоко", "В магазине", Priority.HIGH);

        assertThat(result.getStatus()).isEqualTo(Status.NEW);
        assertThat(result.getTitle()).isEqualTo("Купить молоко");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void addTask_withoutPriority_shouldUseDefaultPriority() {
        when(appProperties.getMaxTasks()).thenReturn(10);
        when(appProperties.getDefaultPriority()).thenReturn(Priority.MEDIUM);
        when(taskRepository.count()).thenReturn(0L);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Task result = taskService.addTask("Задача", "Описание");

        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void addTask_whenLimitExceeded_shouldThrowException() {
        when(appProperties.getMaxTasks()).thenReturn(10);
        when(taskRepository.count()).thenReturn(10L);

        assertThatThrownBy(() -> taskService.addTask("Задача", "Описание", Priority.HIGH))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Превышен лимит задач");
    }

    @Test
    void addTask_withEmptyTitle_shouldThrowException() {
        when(appProperties.getMaxTasks()).thenReturn(10);
        when(taskRepository.count()).thenReturn(0L);

        assertThatThrownBy(() -> taskService.addTask("", "Описание", Priority.HIGH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    // ==================== update() ====================

    @Test
    void update_whenExists_shouldReturnUpdatedTask() {
        Task existing = buildTask(1L, "Старое", Priority.LOW, Status.NEW);
        Task updated = buildTask(1L, "Новое", Priority.HIGH, Status.DONE);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        Optional<Task> result = taskService.update(1L, updated);

        assertThat(result).isPresent();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_whenNotExists_shouldReturnEmpty() {
        Task updated = buildTask(999L, "Новое", Priority.HIGH, Status.DONE);
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.update(999L, updated);

        assertThat(result).isEmpty();
        verify(taskRepository, never()).save(any());
    }

    // ==================== updateStatus() ====================

    @Test
    void updateStatus_whenExists_shouldUpdateStatusOnly() {
        Task task = buildTask(1L, "Задача", Priority.LOW, Status.NEW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Optional<Task> result = taskService.updateStatus(1L, Status.DONE);

        assertThat(result).isPresent();
        verify(taskRepository).save(any(Task.class));
    }

    // ==================== delete() ====================

    @Test
    void delete_shouldCallDeleteById() {
        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    // ==================== helper ====================

    private Task buildTask(Long id, String title, Priority priority, Status status) {
        Task task = new Task(title, "Описание", priority);
        task.setId(id);
        task.setStatus(status);
        return task;
    }
}