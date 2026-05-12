package com.example.taskmanager.config;

import com.example.taskmanager.model.Priority;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${app.name}")
    private String appName;

    @Value("${app.max-tasks}")
    private int maxTasks;

    @Value("${app.default-priority}")
    private String defaultPriorityStr;

    private Priority defaultPriority;

    // R9
    @PostConstruct
    public void init() {
        this.defaultPriority = Priority.valueOf(defaultPriorityStr.toUpperCase());
        System.out.println("=== AppProperties загружены ===");
        System.out.println("app.name: " + appName);
        System.out.println("app.max-tasks: " + maxTasks);
        System.out.println("app.default-priority: " + defaultPriority);
    }

    public String getAppName() { return appName; }
    public int getMaxTasks() { return maxTasks; }
    public Priority getDefaultPriority() { return defaultPriority; }
}