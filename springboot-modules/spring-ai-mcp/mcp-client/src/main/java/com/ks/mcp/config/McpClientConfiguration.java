package com.ks.mcp.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.Tool;

@Configuration
public class McpClientConfiguration {
    final Logger logger = LoggerFactory.getLogger(McpClientConfiguration.class);

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentName;
    @Value("${spring.ai.azure.openai.chat.options.temperature}")
    private String temperature;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    private Set<String> getTools(List<McpSyncClient> mcpSyncClients) {
        final String MONITORING_TOOL_NAME = "event-resolver-client - event-resolver-mcp";

        return mcpSyncClients.stream()
                .filter(e -> e.getClientInfo().name().equalsIgnoreCase(MONITORING_TOOL_NAME))
                .map(e -> e.listTools().tools())
                .flatMap(List::stream)
                .map(Tool::name)
                .collect(Collectors.toSet());
    }

    @Bean
    public ChatModel chatClient(@Autowired List<McpSyncClient> mcpSyncClients) {
        AzureOpenAiChatOptions azureOpenAiChatOptions = AzureOpenAiChatOptions.builder()
                .deploymentName(deploymentName)
                .toolNames(getTools(mcpSyncClients))
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .temperature(Double.parseDouble(temperature))
                .build();

        OpenAIClientBuilder openAIClientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint);

        AzureOpenAiChatModel chatModel = AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(azureOpenAiChatOptions)
                .build();

        return chatModel;
    }
}
