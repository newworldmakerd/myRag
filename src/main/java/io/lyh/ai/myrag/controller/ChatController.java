package io.lyh.ai.myrag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/test")
    public Map<String, String> test(
            @RequestBody ChatRequest request
    ) {
        String answer = chatClient.prompt()
                .user(request.question)
                .call()
                .content();

        return Map.of("answer", answer == null ? "" : answer);
    }

    public record ChatRequest(String question) {
    }

}
