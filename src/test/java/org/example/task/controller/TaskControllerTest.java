package org.example.task.controller;

import org.example.task.dto.TaskRequest;
import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.example.task.repository.TaskRepository;
import org.example.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hamcrest.Matchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    private final UUID existingId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private final UUID nonExistentId = UUID.randomUUID();

    //Тесты CRUD

    //Тест 1: Успешное создание
    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest("Новая задача", "Описание задачи", TaskStatus.TODO, LocalDateTime.now().plusDays(1));
        Task createdTask = new Task(existingId, "Новая задача", "Описание задачи", TaskStatus.TODO, LocalDateTime.now(), request.dueDate());

        when(taskService.create(any(TaskRequest.class))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Новая задача"));
    }

    //Тест 2: Получение несуществующей задачи
    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(taskService.findById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    //Тесты Валидации
    //Тест валидации 1: Пустой заголовок
    @Test
    void shouldReturn400WhenTitleIsEmpty() throws Exception {
        TaskRequest request = new TaskRequest("", "Desc", TaskStatus.TODO, null);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].message",
                        Matchers.hasItem("Title is required")));
    }

    //Тест валидации 2: Заголовок слишком короткий
    @Test
    void shouldReturn400WhenTitleIsTooShort() throws Exception {
        TaskRequest request = new TaskRequest("A", "Desc", TaskStatus.TODO, null);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].message",
                        Matchers.hasItem("Title must be between 3 and 100 characters")));
    }

    //Тест валидации 3: Status is null
    @Test
    void shouldReturn400WhenStatusIsNull() throws Exception {
        String jsonPayload = "{\"title\": \"Valid Title\", \"description\": \"Valid Desc\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'status')].message").value("Status is required"));
    }
}