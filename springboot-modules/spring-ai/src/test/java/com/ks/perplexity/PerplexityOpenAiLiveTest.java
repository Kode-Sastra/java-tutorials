package com.ks.perplexity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("perplexity")
public class PerplexityOpenAiLiveTest {

    private final Logger logger = LoggerFactory.getLogger(PerplexityOpenAiLiveTest.class);

    @Autowired
    private ChatService chatservice;

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    void whenPerplexityChatClientAutoConfigured_thenExecute() {
        String userPrompt = """
            Context:
            1. The book "The Stand" is a fantasy novel by Stephen King.
            2. "Sense and Sensibility" is a romance novel by Jane Austen,
                published by Penguin Books.
            3. "The ABC Murders" is a mystery novel by Agatha Christie,
                published by HarperCollins.
            Question:
            Name a book written by Agatha Christie,
            based strictly on the context above.
            """;
        String response = chatservice.chat(userPrompt);
        logger.info("Response from Perplexity: {}", response);
        assertThat(response).isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("The ABC Murders")
            .doesNotContainIgnoringCase("The Stand", "Sense and Sensibility");
    }

    @Test
    void whenPerplexityChatClientAutoConfigured_thenExamineClientProperties() {
        ChatOptions chatOptions = openAiChatModel.getDefaultOptions();

        assertThat(chatOptions).isInstanceOf(OpenAiChatOptions.class);

        assertThat(chatOptions.getModel()).isEqualTo("sonar-pro");

        assertThat(chatOptions.getTemperature()).isEqualTo(0.2);
    }

    @Test
    void whenPerplexityChatClientCustomized_thenExecute() {
        String userPrompt = """
            Who won the latest FIFA World Cup? Just the name of the country without any citations, please.
            """;
        String response = chatservice.chat(userPrompt, "sonar", "0.1");
        logger.info("Response from Perplexity: {}", response);
        assertThat(response).isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("Argentina");
    }
}
