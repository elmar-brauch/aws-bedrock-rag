package de.bsi.chatbot.chat.conversation;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(indexes = @Index(name = "userid_index", columnList = "userId"))
public class ConversationMessageDAO implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private long timestamp;

    private String userId;

    @Column(columnDefinition = "text")
    private String content;

    private MessageType type;

    public ConversationMessageDAO(String userId, Message message) {
        this.userId = userId;
        this.content = message.getContent();
        this.type = message.getMessageType();
        this.timestamp = System.currentTimeMillis();
    }

}
