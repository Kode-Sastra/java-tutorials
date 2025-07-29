package com.ks.perplexity;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

public class ChatService {
    private OpenAiChatModel chatModel;

    public ChatService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String userInstruction) {
        chatModel.call(userInstruction);
        return chatModel.call(userInstruction);
    }

    public String chat(String userInstruction, String model, String temperature) {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
            .model(model)
            .temperature(Double.parseDouble(temperature))
            .build();
        Prompt prompt = Prompt.builder()
            .content(userInstruction)
            .chatOptions(chatOptions)
            .build();
        return chatModel.call(prompt)
            .getResult()
            .getOutput()
            .getText();
    }
}
