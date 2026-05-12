package org.example.task.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    private final UUID taskId;

    public TaskNotFoundException(UUID taskId) {
        super("Task with ID " + taskId + " not found.");
        this.taskId = taskId;
    }

    public UUID getTaskId() {
        return taskId;
    }
}