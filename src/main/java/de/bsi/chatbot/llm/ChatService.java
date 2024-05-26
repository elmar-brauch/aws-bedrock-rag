package de.bsi.chatbot.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.bedrock.titan.BedrockTitanChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final BedrockTitanChatClient chatClient;
    private final VectorStore vectorStore;

    private static final SystemPromptTemplate template = new SystemPromptTemplate("""
            Du assistierst bei Fragen zum Internetanschluss.
            
            Verwende die Informationen aus dem Abschnitt DOKUMENTE, um genaue Antworten zu geben,
            aber tu so, als ob du diese Informationen von Natur aus wüsstest.
            Wenn du dir nicht sicher bist, gib einfach an, dass du es nicht weißt.
            
            DOKUMENTE:
            {documents}
            
            """);

    // Based on https://docs.spring.io/spring-ai/reference/api/clients/bedrock/bedrock-titan.html
    public void chat() {
        var message = "Meine Firma ist in einem Dorf. Mit welchem Internetanschluss surfe ich am schnellsten?";
        var userMessage = new UserMessage(message);

        var similarDocuments = vectorStore.similaritySearch(message)
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));
        var contextMessage = template.createMessage(Map.of("documents", similarDocuments));

        var prompt = new Prompt(List.of(contextMessage, userMessage));
        var aiResponse = chatClient.call(prompt);
        log.info("AI response: {}", aiResponse.getResult().getOutput().getContent());
    }

}
