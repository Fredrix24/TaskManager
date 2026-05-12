package com.example.taskmanager;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User("admin@test.com", encoder.encode("admin123"), Role.ADMIN);
                userRepository.save(admin);
                User user = new User("user@test.com", encoder.encode("user123"), Role.USER);
                userRepository.save(user);
                System.out.println("=== Тестовые пользователи созданы ===");
            }
        };
    }
}