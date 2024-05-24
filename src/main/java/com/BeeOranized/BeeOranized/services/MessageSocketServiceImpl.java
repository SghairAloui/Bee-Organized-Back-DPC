package com.BeeOranized.BeeOranized.services;



import com.BeeOranized.BeeOranized.Dtos.ConversationResponse;
import com.BeeOranized.BeeOranized.Dtos.MessageRequest;
import com.BeeOranized.BeeOranized.Dtos.MessageResponse;
import com.BeeOranized.BeeOranized.Dtos.WebSocketResponse;
import com.BeeOranized.BeeOranized.Entity.Conversation;
import com.BeeOranized.BeeOranized.Entity.Message;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.Repository.ConversationRepository;
import com.BeeOranized.BeeOranized.Repository.MessageRepository;
import com.BeeOranized.BeeOranized.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of the MessageSocketService interface that handles real-time messaging functionality using web sockets.
 */

/**
 * Implementation of the MessageSocketService interface that handles real-time messaging functionality using web sockets.
 */
@Service
@RequiredArgsConstructor
public class MessageSocketServiceImpl {
  private final SimpMessagingTemplate messagingTemplate;
  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;

  /**
   * Send user conversations to a specific user by their user ID through a web socket.
   *
   * @param userId The ID of the user for whom to send conversations.
   */
  public void sendUserConversationByUserId(int userId) {
    List<ConversationResponse> conversation = conversationRepository.findConversationsByUserId(userId);
    messagingTemplate.convertAndSend(
      "/topic/user/".concat(String.valueOf(userId)),
      WebSocketResponse.builder()
        .type("ALL")
        .data(conversation)
        .build()
    );
  }

  /**
   * Send messages of a specific conversation to the connected users through a web socket.
   *
   * @param conversationId The ID of the conversation for which to send messages.
   */
  public void sendMessagesByConversationId(int conversationId) {
    Conversation conversation = new Conversation();
    conversation.setConversationId((long) conversationId);
    List<Message> messageList = messageRepository.findAllByConversation(conversation);
    List<MessageResponse> messageResponseList = messageList.stream()
      .map((message -> MessageResponse.builder()
        .messageId(message.getMessageId())
        .message(message.getMessage())
        .timestamp(Date.from(message.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()))
        .senderId(message.getSender().getUserId())
        .receiverId(message.getReceiver().getUserId())
        .build())
      ).toList();
    messagingTemplate.convertAndSend("/topic/conv/".concat(String.valueOf(conversationId)), WebSocketResponse.builder()
      .type("ALL")
      .data(messageResponseList)
      .build()
    );
  }

  /**
   * Save a new message using a web socket.
   *
   * @param msg The MessageRequest object containing the message details to be saved.
   */

  public void saveMessage(MessageRequest msg) {
    // Convertir les identifiants de l'expéditeur et du destinataire en Long
    Integer senderId = msg.getSenderId();
    Integer receiverId = msg.getReceiverId();

    // Recherche de l'expéditeur et du destinataire dans le référentiel d'utilisateurs
    User sender = userRepository.findById(Long.valueOf(senderId))
            .orElseThrow(() -> new NoSuchElementException("Utilisateur with ID " + senderId + " not found"));
    User receiver = userRepository.findById(Long.valueOf(receiverId))
            .orElseThrow(() -> new NoSuchElementException("Utilisateur with ID " + receiverId + " not found"));
    Conversation conversation = conversationRepository.findConversationByUsers(sender, receiver)
            .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

    Message newMessage = new Message();
    newMessage.setMessage(msg.getMessage());
    newMessage.setTimestamp(msg.getTimestamp());
    newMessage.setConversation(conversation);
    newMessage.setSender(sender);
    newMessage.setReceiver(receiver);

    Message savedMessage = messageRepository.save(newMessage);

    // notify listener
    MessageResponse res = MessageResponse.builder()
            .messageId(savedMessage.getMessageId())
            .message(savedMessage.getMessage())
            .timestamp(Date.from(savedMessage.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()))
            .senderId(savedMessage.getSender().getUserId())
            .receiverId(savedMessage.getReceiver().getUserId())
            .build();

    messagingTemplate.convertAndSend("/topic/conv/".concat(msg.getConversationId().toString()),
            WebSocketResponse.builder()
                    .type("ADDED")
                    .data(res)
                    .build()
    );

    sendUserConversationByUserId(Math.toIntExact(msg.getSenderId()));
    sendUserConversationByUserId(Math.toIntExact(msg.getReceiverId()));
  }
  /**
   * Delete a conversation by its unique conversation ID using a web socket.
   *
   * @param conversationId The ID of the conversation to be deleted.
   */
  @Transactional
  public void deleteConversationByConversationId(int conversationId) {
    Conversation c = new Conversation();
    c.setConversationId((long) conversationId);
    messageRepository.deleteAllByConversation(c);
    conversationRepository.deleteById((long) conversationId);
  }

  /**
   * Delete a message by its unique message ID within a conversation using a web socket.
   *
   * @param conversationId The ID of the conversation to notify its listener.
   * @param messageId      The ID of the message to be deleted.
   */
  public void deleteMessageByMessageId(int conversationId, int messageId) {
    messageRepository.deleteById((long) messageId);
    // notify listener
    sendMessagesByConversationId(conversationId);
  }
}
