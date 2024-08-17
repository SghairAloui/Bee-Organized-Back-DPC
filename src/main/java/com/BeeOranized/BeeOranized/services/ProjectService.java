package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProjectRepository projectRepository;
    @Transactional
    public Project createProject(Project project) {
        Project createdProject = projectRepository.save(project);

        // Send email notification to assigned users
        sendProjectNotification(createdProject);

        return createdProject;
    }
    private void sendProjectNotification(Project project) {
        // Fetch assigned users' email addresses from the project entity
        List<String> assignedUsers = project.getAssignedUsers();

        // Préparer le contenu de l'email
        String subject = "Nouveau Projet Créé : " + project.getTitle();
        String message = "<html>" +
                "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f9;'>" +
                "<div style='background-color: #f4f4f9; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1);'>" +
                "<div style='text-align: center; padding-bottom: 20px;'>" +
                "</div>" +
                "<h2 style='color: #0066cc; text-align: center;'>Nouveau Projet Créé</h2>" +
                "<p style='color: #333333;'>Cher utilisateur,</p>" +
                "<p style='color: #333333;'>Un nouveau projet a été créé : <strong>" + project.getTitle() + "</strong>.</p>" +
                "<p style='color: #333333;'>Vous êtes assigné à ce projet en tant que membre de l'équipe.</p>" +
                "<div style='background-color: #e7f3fe; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p style='margin: 0; color: #333333;'><strong>Title :</strong> " + project.getTitle() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Description :</strong> " + project.getDescription() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Date de début :</strong> " + project.getStartDate() + "</p>" +
                "<p style='margin: 0; color: #333333;'><strong>Date de fin :</strong> " + project.getEndDate() + "</p>" +
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
        for (String userEmail : assignedUsers) {
            emailService.sendEmail(userEmail, subject, message);
        }
    }

    public List<Project> getProjectsByScrumMaster(String scrumMaster){
        return projectRepository.findByScrumMaster(scrumMaster);
    }
    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }
    public Project updateProject(Project project){
        return projectRepository.save(project);
    }
    public void deleteProject(Long id){
        projectRepository.deleteById(id);
    }
   /* public List<Project> getProjectsByUser(String user){
        return projectRepository.findByAssignedUsersContaining(user);
    }*/
    public List<Project> getProjectsByUserorScrumMaster(String assignedUsers, String scrumMaster){
        return projectRepository.findByAssignedUsersContainingOrScrumMaster(assignedUsers, scrumMaster);
    }
    public List<Project> getProjectsByAssignedUsers(String assignedUsers){
        return projectRepository.findByAssignedUser(assignedUsers);
    }
    public List<Project> getProjectsByAssignedUserOrScrumMaster(String assignedUsers, String scrumMaster){
        return projectRepository.findByAssignedUsersContainingOrScrumMaster(assignedUsers, scrumMaster);
    }
    public Project updateProjectbyid(Long id, Project project){
        project.setId(id);
        return projectRepository.save(project);
    }
    public Project getProjectById(Long id){
        return projectRepository.findById(id).orElse(null);
    }

}
