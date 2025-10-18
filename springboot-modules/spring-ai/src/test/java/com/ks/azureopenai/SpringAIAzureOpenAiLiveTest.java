package com.ks.azureopenai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("azureopenai")
public class SpringAIAzureOpenAiLiveTest {
    final Logger logger = LoggerFactory.getLogger(SpringAIAzureOpenAiLiveTest.class);
    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatService customChatService;

    @Autowired
    private VectorDBService vectorDBStore;

    @BeforeAll
    void setup() {
        Document doc1 = new Document("""
              TechVerse Solutions provides tailor-made
              cloud integration services for finance and retail companies.
            """);
        Document doc2 = new Document("""
            The company’s AI-powered analytics platform delivers actionable
            insights to help customers optimize their business operations.
            """);
        Document doc3 = new Document("""
            At TechVerse Solutions, dedicated experts support clients with 24/7
            monitoring and rapid troubleshooting.
            """);

        List<Document> docs = List.of(doc1, doc2, doc3);
        vectorDBStore.saveDocs(docs);
        logger.info("Documents added to vector store");
    }

    @Test
    void givenProgrammaticallyConfiguredClient_whenQueryAzureOpenAiDeployment_thenReturnResponse() {
        String query = "TechVerse Solutions provides cloud " +
            "integration services for what type of companies?";
        String context = vectorDBStore.fetchContextFromVectorDB(query);
        logger.info("context fetched: {}", context);
        String prompt = """
            Context: {context}
            Question: {question}
            Instructions:
            Using the provided context, answer the question in a concise manner.
            If the context does not contain the answer, respond with "I don't know".
            """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(prompt)
            .variables(Map.of("context", context, "question", query))
            .build();
        Prompt finalPrompt = promptTemplate.create();

        logger.info("finalPrompt: {}", finalPrompt.getContents());

        String response = customChatService.chat(finalPrompt);

        logger.info("response: {}", response);

        assertThat(response)
            .isNotNull()
            .containsIgnoringCase("Finance")
            .containsIgnoringCase("Retail");
    }

    @Test
    void givenAutoConfiguredClient_whenQueryAzureOpenAiDeployment_thenReturnResponse() {
        String query = "TechVerse Solutions provides cloud " +
            "integration services for what type of companies?";
        String context = vectorDBStore.fetchContextFromVectorDB(query);
        logger.info("context fetched: {}", context);
        String prompt = """
            Context: {context}
            Question: {question}
            Instructions:
            Using the provided context, answer the question in a concise manner.
            If the context does not contain the answer, respond with "I don't know".
            """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(prompt)
            .variables(Map.of("context", context, "question", query))
            .build();
        Prompt finalPrompt = promptTemplate.create();

        logger.info("finalPrompt: {}", finalPrompt.getContents());

        String response = chatService.chat(finalPrompt);

        logger.info("response: {}", response);

        assertThat(response)
            .isNotNull()
            .containsIgnoringCase("Finance")
            .containsIgnoringCase("Retail");
    }
}
