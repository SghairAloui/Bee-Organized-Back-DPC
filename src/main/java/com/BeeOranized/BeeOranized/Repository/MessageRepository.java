package com.BeeOranized.BeeOranized.Repository;



import com.BeeOranized.BeeOranized.Entity.Conversation;
import com.BeeOranized.BeeOranized.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByConversation(Conversation conversation);

    void deleteAllByConversation(Conversation conversation);
}
