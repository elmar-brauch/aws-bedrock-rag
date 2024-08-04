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
@Table(indexes = @Index(name = "chatid_index", columnList = "chatId"))
public class ConversationMessageDAO implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private long timestamp;

    private String chatId;

    @Column(columnDefinition = "text")
    private String content;

    private MessageType type;

    public ConversationMessageDAO(String chatId, Message message) {
        this.chatId = chatId;
        this.content = message.getContent();
        this.type = message.getMessageType();
        this.timestamp = System.currentTimeMillis();
    }

}
