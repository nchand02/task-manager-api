package com.encora.taskmanager.service;

import com.encora.taskmanager.entity.Task;
import com.encora.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

	private final TaskRepository taskRepository;

	@Autowired
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
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

	public Task createTask(Task task) {
		validateTask(task);
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