package com.ks.azureopenai;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

@Configuration
@Profile("azureopenai")
public class ApplicationConfiguration {
    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Bean
    public ChatService chatService(AzureOpenAiChatModel azureOpenAiChatModel) {
        return new ChatService(azureOpenAiChatModel);
    }

    @Bean
    public ChatService customChatService() {
        OpenAIClientBuilder openAIClientBuilder = new OpenAIClientBuilder()
            .credential(new AzureKeyCredential(getCredential()))
            .endpoint(getEndpoint());

        AzureOpenAiChatOptions openAIChatOptions = AzureOpenAiChatOptions.builder()
            .deploymentName("gpt-5-nano")
            .temperature(1d)
            .build();

        AzureOpenAiChatModel chatModel = AzureOpenAiChatModel.builder()
            .openAIClientBuilder(openAIClientBuilder)
            .defaultOptions(openAIChatOptions)
            .build();
        return new ChatService(chatModel);
    }

    private String getCredential() {
        return apiKey;
    }

    private String getEndpoint() {
        return endpoint;
    }

    @Bean
    VectorDBService vectorDBStore(AzureOpenAiEmbeddingModel embeddingModel) {
        return new VectorDBService(SimpleVectorStore.builder(embeddingModel)
            .build()
        );
    }
}
