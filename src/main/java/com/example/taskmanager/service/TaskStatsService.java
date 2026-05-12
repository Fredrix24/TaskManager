package com.example.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype") // R13
public class TaskStatsService {

    private final String uuid;

    public TaskStatsService() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }
}