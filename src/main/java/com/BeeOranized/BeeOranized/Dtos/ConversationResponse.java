package com.BeeOranized.BeeOranized.Dtos;

import java.sql.Timestamp;

public interface ConversationResponse {

    Long getConversationId();

  Long getOtherUserId();

    String getOtherUserName();

    String getLastMessage();

    Timestamp getLastMessageTimestamp();
}
