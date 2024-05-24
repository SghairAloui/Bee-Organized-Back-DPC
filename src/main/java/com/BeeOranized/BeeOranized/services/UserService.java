package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Dtos.ApiResponsee;
import com.BeeOranized.BeeOranized.Dtos.UserDataDTO;
import com.BeeOranized.BeeOranized.Entity.Conversation;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.Repository.ConversationRepository;
import com.BeeOranized.BeeOranized.Repository.UserRepository;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class UserService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    public List<User> getAllUser(){
        return userRepository.findAll();
    }
    public String generateResetPasswordToken(User user) {
        User existingUser = userRepository.findByUserEmail(user.getUserEmail()).orElse(null);
        if (existingUser == null) {
            return null;
        }

        String resetPasswordToken = UUID.randomUUID().toString();
        System.out.println("user reset password is "+resetPasswordToken);

        // Save the token to the user entity
        existingUser.setResetPasswordToken(resetPasswordToken);
        userRepository.save(existingUser);

        return resetPasswordToken;
    }
    private  void sendResetPasswordEmail(User user, String resetPasswordToken) {
        // Prepare email content
        String subject = "Reset Password Request";
        String message = "Dear User,\n\nYou have requested to reset your password.\n" +
                "Please click the link below to reset your password:\n\n" +
                "http://localhost:4200/reset-password?token=" + resetPasswordToken + "\n\n" +
                "If you did not request this, please ignore this email.\n\nRegards,\nYour Team";

        // Send email to the user
        emailService.sendEmail(user.getUserEmail(), subject, message);
    }
    public boolean resetPassword(String userEmail, String resetPasswordToken, String newPassword) {
        Optional<User> optionalExistingUser = userRepository.findByUserEmail(userEmail);
        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            if (!resetPasswordToken.equals(existingUser.getResetPasswordToken())) {
                return false;
            }

            // Save the new password to the user entity
            existingUser.setUserPassword(encoder.encode(newPassword));
            existingUser.setResetPasswordToken(null);
            userRepository.save(existingUser);

            return true;
        } else {
            // Handle the case when the user with the specified email doesn't exist
            return false; // or throw an exception, or return an error message
        }
    }



    public List<UserDataDTO> getAllUsersExcept(Long userId) {
        List<User> users = userRepository.findAllExceptUser(userId);
        return users.stream()
                .map(this::convertToUserDataDTO)
                .collect(Collectors.toList());
    }
    public ResponseEntity<ApiResponsee> findConversationIdByUser1IdAndUser2Id(int user1Id, int user2Id) {
        Long conversationId;
        Optional<User> user1 = userRepository.findById((long) user1Id);
        Optional<User> user2 = userRepository.findById((long) user2Id);
        if (user1.isEmpty() || user2.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponsee.builder()
                            .statusCode(200)
                            .status("Failed")
                            .reason("User not found")
                            .data(null)
                            .build());
        }

        Optional<Conversation> existingConversation = conversationRepository.findConversationByUsers(user1.get(), user2.get());
        if (existingConversation.isPresent()) {
            conversationId = existingConversation.get().getConversationId();
        } else {
            Conversation newConversation = new Conversation();
            newConversation.setUser1(user1.get());
            newConversation.setUser2(user2.get());
            Conversation savedConversation = conversationRepository.save(newConversation);
            conversationId = savedConversation.getConversationId();
        }

        return ResponseEntity.ok()
                .body(ApiResponsee.builder()
                        .statusCode(200)
                        .status("Success")
                        .reason("OK")
                        .data(conversationId)
                        .build());
    }

    public UserDataDTO getUserDataByEmail(String email) {
        Optional<User> userOptional = userRepository.findByUserEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserDataDTO(user);
        } else {
            return null; // Or throw an exception if preferred
        }
    }

    private UserDataDTO convertToUserDataDTO(User user) {
        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setUserId(user.getUserId());

        // Segment the name into firstName and lastName
        String[] nameParts = segmentName(user.getName());
        userDataDTO.setFirstName(nameParts[0]);
        userDataDTO.setLastName(nameParts[1]);

        userDataDTO.setEmail(user.getUserEmail());
        return userDataDTO;
    }

    private String[] segmentName(String fullName) {
        String[] nameParts = fullName.split(" ", 2);
        if (nameParts.length == 1) {
            // If there's no space, use the full name as both first and last name
            return new String[]{nameParts[0], nameParts[0]};
        } else {
            return nameParts;
        }
    }

    }



