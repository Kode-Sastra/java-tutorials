package com.ks.perplexity;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("perplexity")
public class ApplicationConfiguration {

    @Bean
    public ChatService chatService(@Autowired OpenAiChatModel openAiChatModel) {
        return new ChatService(openAiChatModel);
    }
}
