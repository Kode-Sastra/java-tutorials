package com.ks.azureopenai;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;

public class ChatService {

    AzureOpenAiChatModel chatModel;

    public ChatService(AzureOpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(Prompt prompt) {
        return chatModel.call(prompt)
            .getResult()
            .getOutput()
            .getText();
    }
}
