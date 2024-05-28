package de.bsi.chatbot.chat.llm;

import de.bsi.chatbot.chat.conversation.ChatRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private static final String DEFAULT_MESSAGE = "Meine Firma ist in einem Dorf. Mit welchem Internetanschluss surfe ich am schnellsten?";

    @GetMapping("/chat")
    public Map<String, Message> generate(
            @RequestParam(defaultValue = DEFAULT_MESSAGE) String message,
            @RequestParam(required = false) String userId) {
        var aiResponse = chatService.chat(message, userId);
        log.info("AI response: {}", aiResponse.getContent());
        return Map.of("generation", aiResponse);
    }

    @PostMapping("/chat")
    public Map<String, Message> generatePost(@RequestBody ChatRequestDTO chatRequest) {
        return generate(chatRequest.message(), chatRequest.userId());
    }

}
