package com.ks.azureopenai;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;

public class ChatService {

    AzureOpenAiChatModel chatModel;

    public ChatService(AzureOpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String message) {
        return chatModel.call(message);
    }


}
