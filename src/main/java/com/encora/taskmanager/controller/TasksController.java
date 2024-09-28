package com.encora.taskmanager.controller;

import com.encora.taskmanager.dto.UserPrincipal;
import com.encora.taskmanager.entity.Task;
import com.encora.taskmanager.entity.User;
import com.encora.taskmanager.service.TaskService;
import com.encora.taskmanager.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TasksController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserService userService;

	@Autowired 
	private PagedResourcesAssembler<Task> assembler;
	
	@GetMapping("/test")
	public String getMessage() {
		return "Hello World";
	}
	
	@GetMapping
	public ResponseEntity<PagedModel<EntityModel<Task>>> getAllTasksPaginated(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "10") int size) {
		Page<Task> tasksPage = taskService.getAllTasksPaginated(page, size);
		return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(tasksPage));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
		Task task = taskService.getTaskById(id);
		return ResponseEntity.ok(task);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable Long userId) {
		List<Task> tasks = taskService.getTasksByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(tasks);
	}

	@GetMapping("/my-tasks")
	public ResponseEntity<List<Task>> getMyTasks() {
		// Get user details from SecurityContextHolder
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal instanceof UserPrincipal)) { // Cast to UserPrincipal
			// Handle the case where the principal is not of the expected type
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Long userId = ((UserPrincipal) principal).getId();
		List<Task> tasks = taskService.getTasksByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(tasks);
	}
	
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

	@PostMapping("/my-tasks")
	public ResponseEntity<Task>  createMyTask(@RequestBody Task task) {
		// Get the currently authenticated user's ID
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal instanceof UserPrincipal)) {
			throw new RuntimeException("User not found in security context");
		}
		Long userId = ((UserPrincipal) principal).getId();

		// Retrieve the User entity from the database
		User user = userService.findById(userId);
		// Set the User for the task
		task.setUser(user);

		// Save the task
		Task createdTask = taskService.createTask(task);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
	}
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task task = taskService.updateTask(id, updatedTask);
        return ResponseEntity.ok(task);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
