# Task Manager

Spring Boot REST API для управления задачами с веб-интерфейсом.

**Стек:** 
Java 21
Spring Boot 3.2.5
Spring Security + JWT 
Spring Data JPA + H2 / PostgreSQL 
Thymeleaf 
Flyway 
Docker + docker-compose.

Быстрый старт локально (H2 в памяти): 
``bash
mvn clean spring-boot:run 

(откроется на http://localhost:8080). 

### Docker (PostgreSQL): 
cp .env.example .env && docker compose up --build.

**Тестовые пользователи:** 
admin@test.com / admin123 (ADMIN), 
user@test.com / user123 (USER).

### REST API: 
Аутентификация — POST /api/auth/login с {"email":"...","password":"..."}. Задачи — GET /tasks (все), POST /tasks (создать), PATCH /tasks/1/status (обновить статус), DELETE /tasks/1 (только ADMIN). Все запросы с заголовком Authorization: Bearer ТОКЕН. Без токена — 401, USER на DELETE — 403.

### Веб-интерфейс: 
http://localhost:8080/login — просмотр, создание, редактирование задач. Кнопка "Удалить" только для ADMIN.

### Тесты 
``bash
mvn test 
(12 unit-тестов с Mockito, без Spring-контекста).

### Миграции 
Flyway: V1__create_tasks_table.sql (таблицы + тестовые данные), V2__add_created_at.sql (поле created_at).

### Структура: 
config/ (AppConfig, AppProperties), controller/ (AuthController, TaskController, TaskWebController, GlobalExceptionHandler), dto/ (AuthResponse, LoginRequest, RegisterRequest), model/ (Task, User, Priority, Status, Role), repository/ (TaskRepository, UserRepository), security/ (SecurityConfig, JwtAuthFilter, CustomAuthEntryPoint, CustomAccessDeniedHandler), service/ (AuthService, JwtService, TaskService, TaskServiceImpl, TaskStatsService, UserDetailsServiceImpl).

### Docker: 
``bash
docker compose up --build 
(запуск), docker compose down (остановка), docker compose down -v (удалить данные).
