package de.bsi.chatbot.chat.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public String generatePost(@RequestBody ChatRequestDTO chatRequest) {
        log.debug("Following chat message with chatId {} received: {}", chatRequest.chatId(), chatRequest.message());
        var aiResponse = chatService.chat(chatRequest.message(), chatRequest.chatId());
        return aiResponse.getContent();
    }

}
