package com.BeeOranized.BeeOranized.Controller;

import com.BeeOranized.BeeOranized.Dtos.ApiResponsee;
import com.BeeOranized.BeeOranized.Dtos.UserDataDTO;
import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200/")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUser();
    }

    @GetMapping("/email")
    public ResponseEntity<UserDataDTO> getUserDataByEmail(@RequestParam String email) {
        UserDataDTO userData = userService.getUserDataByEmail(email);
        if (userData != null) {
            return ResponseEntity.ok(userData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/except/{userId}")
    public ResponseEntity<ApiResponsee> getAllUsersExcept(@PathVariable Long userId) {
        List<UserDataDTO> users = userService.getAllUsersExcept(userId);
        return ResponseEntity.ok(
                ApiResponsee.builder()
                        .statusCode(200)
                        .status("Success")
                        .data(users)
                        .build()
        );
    }
    /**
     * Retrieve a list of all users except the user with a specific user ID.
     *
     * @param userId The ID of the user to be excluded from the list.
     * @return ResponseEntity containing an ApiResponse with a list of User objects representing all users except the specified user.
     */

    /**
     * Find or create a conversation ID for a pair of users based on their user IDs.
     private final ConversationRepository conversationRepository;
     *
     * @param user1Id The ID of the first user in the conversation.
     * @param user2Id The ID of the second user in the conversation.
     * @return ResponseEntity containing an ApiResponse with the conversation ID for the user pair.
     */
    @GetMapping("/conversation/id")
    public ResponseEntity<ApiResponsee> findConversationIdByUser1IdAndUser2Id(@RequestParam int user1Id, @RequestParam int user2Id) {
        return userService.findConversationIdByUser1IdAndUser2Id(user1Id, user2Id);
    }
}
