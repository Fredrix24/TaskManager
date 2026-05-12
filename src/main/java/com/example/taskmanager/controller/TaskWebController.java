package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/tasks")
public class TaskWebController {

    private final TaskService taskService;

    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", Priority.values());
        return "task/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam Priority priority) {
        taskService.addTask(title, description, priority);
        return "redirect:/web/tasks";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        taskService.findById(id).ifPresent(task -> {
            model.addAttribute("task", task);
            model.addAttribute("priorities", Priority.values());
        });
        return "task/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam Priority priority) {
        taskService.findById(id).ifPresent(task -> {
            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            taskService.update(id, task);
        });
        return "redirect:/web/tasks";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        taskService.delete(id);
        return "redirect:/web/tasks";
    }
}