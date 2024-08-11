package de.bsi.chatbot.chat.llm;

import de.bsi.chatbot.chat.conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final AzureOpenAiChatModel chatModel;
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
    public Message chat(String message, String chatId) {
        var userMessage = new UserMessage(message);
        var prompt = buildPrompt(userMessage, chatId);
        var awsResponse = chatModel.call(prompt);
        return extractAndPersistResponseMessage(awsResponse, chatId);
    }

    private Prompt buildPrompt(UserMessage userMessage, String chatId) {
        return buildPromptForExistingConversation(userMessage, chatId)
                .orElse(buildPromptForNewConversation(userMessage, chatId));
    }

    private Optional<Prompt> buildPromptForExistingConversation(UserMessage userMessage, String chatId) {
        var conversationMessages = new ArrayList<>(conversationService.findPreviousMessages(chatId));
        if (conversationMessages.isEmpty())
            return Optional.empty();
        log.debug("Continuing existing conversation {}", chatId);
        conversationMessages.add(userMessage);
        conversationService.persistMessage(chatId, userMessage);
        return Optional.of(new Prompt(conversationMessages));
    }

    private Prompt buildPromptForNewConversation(UserMessage userMessage, String chatId) {
        log.debug("Starting new conversation {}", chatId);
        var conversationStartMessages = List.of(buildContextMessage(userMessage.getContent()), userMessage);
        conversationService.persistMessages(chatId, conversationStartMessages);
        return new Prompt(conversationStartMessages, buildOptions());
    }

    private Message buildContextMessage(String message) {
        var similarDocuments = vectorStore.similaritySearch(message)
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));
        return template.createMessage(Map.of("documents", similarDocuments));
    }

    private Message extractAndPersistResponseMessage(ChatResponse awsResponse, String chatId) {
        var responseMessage = awsResponse.getResult().getOutput();
        conversationService.persistMessage(chatId, responseMessage);
        return responseMessage;
    }

    private ChatOptions buildOptions() {
        return AzureOpenAiChatOptions.builder()
                .withFunction("checkSaltwaterConnectionWithCityName")
                .withFunction("checkSaltwaterConnectionWithPostalCode")
                .build();
    }

}
