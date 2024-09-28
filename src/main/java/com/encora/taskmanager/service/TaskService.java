package com.encora.taskmanager.service;

import com.encora.taskmanager.dto.UserPrincipal;
import com.encora.taskmanager.entity.Task;
import com.encora.taskmanager.entity.User;
import com.encora.taskmanager.repository.TaskRepository;
import com.encora.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

	private final TaskRepository taskRepository;

	private UserRepository userRepository;

	@Autowired
	public TaskService(TaskRepository taskRepository,UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}



	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}
	
    public Page<Task> getAllTasksPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findAll(pageable);
    }

	public Task getTaskById(Long id) {
		return taskRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
	}

	public List<Task> getTasksByUserId(Long userId) {
		return taskRepository.findByUserId(userId);
	}

	public Task createTask(Task task) {
		validateTask(task);
		return taskRepository.save(task);
	}

	public Task createMyTask(Task task) {
		// Get the currently authenticated user's ID
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal instanceof UserPrincipal)) {
			throw new RuntimeException("User not found in security context");
		}
		Long userId = ((UserPrincipal) principal).getId();

		// Retrieve the User entity from the database
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		// Set the User for the task
		task.setUser(user);

		// Validate the task
		validateTask(task);

		// Save the task
		return taskRepository.save(task);
	}

	public Task updateTask(Long id, Task updatedTask) {
		Task existingTask = taskRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

		existingTask.setTitle(updatedTask.getTitle());
		existingTask.setDescription(updatedTask.getDescription());
		existingTask.setCompleted(updatedTask.isCompleted());

		validateTask(existingTask);
		return taskRepository.save(existingTask);
	}

	public void deleteTask(Long id) {
		taskRepository.deleteById(id);
	}

	private void validateTask(Task task) {
		if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
			throw new IllegalArgumentException("Task title cannot be empty.");
		}
		// Add other validations as needed
	}
}