package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Derived queries
    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);
    List<Task> findByStatusAndPriority(Status status, Priority priority);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    long countByStatus(Status status);

    // JPQL
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Task> searchByKeyword(@Param("kw") String keyword);
}