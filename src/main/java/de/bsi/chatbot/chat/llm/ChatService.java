package de.bsi.chatbot.chat.llm;

import de.bsi.chatbot.chat.conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.bedrock.titan.BedrockTitanChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final BedrockTitanChatClient chatClient;
    private final VectorStore vectorStore;
    private final ConversationService conversationService;

    private static final SystemPromptTemplate template = new SystemPromptTemplate("""
            Du assistierst bei Fragen zum Internetanschluss.
            
            Verwende die Informationen aus dem Abschnitt DOKUMENTE, um genaue Antworten zu geben,
            aber tu so, als ob du diese Informationen von Natur aus wüsstest.
            Wenn du dir nicht sicher bist, gib einfach an, dass du es nicht weißt.
            
            DOKUMENTE:
            {documents}
            
            """);

    // Based on https://docs.spring.io/spring-ai/reference/api/clients/bedrock/bedrock-titan.html
    public Message chat(String message, String userId) {
        var userMessage = new UserMessage(message);
        var prompt = buildPrompt(userMessage, userId);
        var awsResponse = chatClient.call(prompt);
        var responseMessage = awsResponse.getResult().getOutput();
        conversationService.persistMessage(userId, responseMessage);
        return responseMessage;
    }

    private Prompt buildPrompt(UserMessage userMessage, String userId) {
        var conversationMessages = new ArrayList<>(conversationService.findPreviousMessages(userId));

        if (!conversationMessages.isEmpty()) {
            log.debug("Continuing existing conversation of user {}", userId);
            conversationMessages.add(userMessage);
            conversationService.persistMessage(userId, userMessage);
            return new Prompt(conversationMessages);
        }

        log.debug("Starting new conversation of user {}", userId);
        var conversationStartMessages = List.of(buildContextMessage(userMessage.getContent()), userMessage);
        conversationService.persistMessages(userId, conversationStartMessages);
        return new Prompt(conversationStartMessages);
    }

    private Message buildContextMessage(String message) {
        var similarDocuments = vectorStore.similaritySearch(message)
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));
        return template.createMessage(Map.of("documents", similarDocuments));
    }

}
