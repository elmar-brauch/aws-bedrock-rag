package de.bsi.chatbot.chat.conversation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public void persistMessages(String chatId, List<Message> messages) {
        if (messages != null)
            messages.forEach(message -> persistMessage(chatId, message));
    }

    public void persistMessage(String chatId, Message message) {
        if (StringUtils.hasText(chatId) && message != null) {
            conversationRepository.save(new ConversationMessageDAO(chatId, message));
            log.debug("For user {} the following {} message is persisted: {}",
                    chatId, message.getMessageType(), message.getContent());
        }
    }

    public List<Message> findPreviousMessages(String chatId) {
        if (StringUtils.hasText(chatId)) {
            return conversationRepository.findByChatIdOrderByTimestampAsc(chatId)
                    .stream()
                    .map(this::mapMessage)
                    .toList();
        }
        return List.of();
    }

    private Message mapMessage(ConversationMessageDAO dao) {
        return switch (dao.getType()) {
            case USER -> new UserMessage(dao.getContent());
            case SYSTEM -> new SystemMessage(dao.getContent());
            case ASSISTANT -> new AssistantMessage(dao.getContent());
            case FUNCTION -> new FunctionMessage(dao.getContent());
        };
    }

}
