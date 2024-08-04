package de.bsi.chatbot.chat.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<ConversationMessageDAO, Long> {

    List<ConversationMessageDAO> findByChatIdOrderByTimestampAsc(String userId);

}
