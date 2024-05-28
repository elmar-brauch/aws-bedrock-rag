package de.bsi.chatbot;

import de.bsi.chatbot.dataimport.DocumentImport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(ChatbotApplication.class, args);
        context.getBean(DocumentImport.class).importDocuments();
    }

}
