package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.Task;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import com.BeeOranized.BeeOranized.Repository.TaskRipository;
import com.BeeOranized.BeeOranized.enums.taskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRipository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmailService emailService;
    @Transactional
    public Task createTask(Task task) {
        // Ensure the task is associated with a project
        if (task.getProject() == null || task.getProject().getId() == null) {
            return null;
        }

        // Set default status if not provided
        if (task.getStatus() == null) {
            task.setStatus(taskStatus.NEW);
        }

        // Set current date as startDate if not provided
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDate.now());
        }

        // Save the task
        Task createdTask = taskRepository.save(task);

        // Fetch the project from the database and update its task list
        Project project = projectRepository.findById(task.getProject().getId()).orElse(null);
        if (project != null) {
            project.getTasks().add(createdTask);
            projectRepository.save(project);
        }

        // Send email notification to assigned users
        sendTaskNotification(createdTask);
        return createdTask;
    }



    private void sendTaskNotification(Task task) {
        // Fetch assigned users' email addresses from the project entity
        List<String> assignedUser = Collections.singletonList(task.getAssignedUser());

        // Préparer le contenu de l'email
        String subject = "Nouvelle Tâche Assignée : " + task.getTitle();
        String message = "<html>" +
                "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f9;'>" +
                "<div style='background-color: #f4f4f9; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1);'>" +
                "<div style='text-align: center; padding-bottom: 20px;'>" +
                "</div>" +
                "<h2 style='color: #ff6600; text-align: center;'>Nouvelle Tâche Assignée</h2>" +
                "<p style='color: #333333;'>Cher utilisateur,</p>" +
                "<p style='color: #333333;'>Une nouvelle tâche a été assignée : <strong>" + task.getTitle() + "</strong>.</p>" +
                "<p style='color: #333333;'>Vous êtes assigné à cette tâche dans le projet.</p>" +
                "<div style='background-color: #ffe6cc; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p style='margin: 0; color: #333333;'><strong>Title :</strong> " + task.getTitle() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Description :</strong> " + task.getDescription() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Date de début :</strong> " + task.getStartDate() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Date de fin :</strong> " + task.getEndDate() + "</p>" +
                "</div>" +
                "<p style='color: #333333;'>Cordialement,<br>Votre Équipe</p>" +
                "<hr style='border: 0; height: 1px; background-color: #eeeeee; margin: 20px 0;'>" +
                "<div style='text-align: center;'>" +
                "<p style='color: #aaaaaa; font-size: 12px;'>© 2024 Votre Société. Tous droits réservés.</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";


        // Send email to each assigned user
        for (String userEmail : assignedUser) {
            emailService.sendEmail(userEmail, subject, message);
        }
    }



    public Task updateTaskProgress(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        // Fetch the task from the database
        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        // Update the task status
        existingTask.setStatus(taskStatus.PENDING);
        return taskRepository.save(existingTask);
    }
    public Task updateTaskTesting(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        // Update the task status
        existingTask.setStatus(taskStatus.BLOQUED);
        return taskRepository.save(existingTask);
    }
    public Task updateTaskComplete(Task task) {
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        existingTask.setStatus(taskStatus.FINISHED);
        return taskRepository.save(existingTask);
    }
    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }
    public Task updateTask(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignedUser(task.getAssignedUser());
        existingTask.setStartDate(task.getStartDate());
        existingTask.setEndDate(task.getEndDate());
        existingTask.setStatus(task.getStatus());
        return taskRepository.save(existingTask);
    }
    public boolean deleteTask(Long id) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask == null) {
            return false;
        }

        Project project = projectRepository.findById(existingTask.getProject().getId()).orElse(null);
        if (project != null) {
            project.getTasks().remove(existingTask);
            projectRepository.save(project);
        }

        taskRepository.deleteById(id);
        return true;
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }
}