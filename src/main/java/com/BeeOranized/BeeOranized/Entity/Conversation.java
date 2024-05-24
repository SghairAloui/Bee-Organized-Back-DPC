package com.BeeOranized.BeeOranized.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.BeeOranized.BeeOranized.Entity.User;


import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation")
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "conversation_id")
  private Long conversationId;

  @ManyToOne
  @JoinColumn(name = "user1_id")
  private User user1;

  @ManyToOne
  @JoinColumn(name = "user2_id")
  private User user2;


}

