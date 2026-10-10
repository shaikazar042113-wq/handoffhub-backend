package com.handoffhub.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Task> all() { return repository.findAllByOrderByUpdatedAtDesc(); }

    @GetMapping("/{id}")
    public Task one(@PathVariable Long id) { return findTask(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody Task task) {
        task.setStatus(task.getStatus() == null ? TaskStatus.TODO : task.getStatus());
        task.setPriority(task.getPriority() == null ? TaskPriority.MEDIUM : task.getPriority());
        return repository.save(task);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody Task incoming) {
        Task task = findTask(id);
        task.setTitle(incoming.getTitle());
        task.setDescription(incoming.getDescription());
        task.setAssignee(incoming.getAssignee());
        task.setStatus(incoming.getStatus() == null ? TaskStatus.TODO : incoming.getStatus());
        task.setPriority(incoming.getPriority() == null ? TaskPriority.MEDIUM : incoming.getPriority());
        task.setDueDate(incoming.getDueDate());
        return repository.save(task);
    }

    @PostMapping("/{id}/handoff")
    public Task handoff(@PathVariable Long id, @Valid @RequestBody HandoffRequest request) {
        Task task = findTask(id);
        task.setPreviousOwner(task.getAssignee());
        task.setAssignee(request.nextAssignee());
        task.setHandoffNote(request.note());
        if (task.getStatus() == TaskStatus.DONE) task.setStatus(TaskStatus.IN_PROGRESS);
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { repository.delete(findTask(id)); }

    private Task findTask(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
}
