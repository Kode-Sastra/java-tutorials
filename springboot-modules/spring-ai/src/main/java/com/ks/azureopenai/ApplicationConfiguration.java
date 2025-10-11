package com.ks.azureopenai;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("azureopenai")
public class ApplicationConfiguration {

    @Bean
    public ChatService chatService(@Autowired AzureOpenAiChatModel azureOpenAiChatModel) {
        return new ChatService(azureOpenAiChatModel);
    }

}
