package com.encora.taskmanager.controller;

import com.encora.taskmanager.entity.Task;
import com.encora.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TasksController {

	@Autowired
	private TaskService taskService;
	
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
	
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
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
