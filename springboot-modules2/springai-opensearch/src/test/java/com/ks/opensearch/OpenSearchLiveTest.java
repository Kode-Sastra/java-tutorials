package com.ks.opensearch;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.opensearch.OpenSearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OpenSearchLiveTest {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchLiveTest.class);

    @Autowired
    private OpenSearchVectorStore vectorStore;

    @Value("classpath:/rag/spring-framework.pdf")
    private String filePath;

    @Autowired
    private AzureOpenAiChatModel azureOpenAiChatModel;

    @Test
    @Order(1)
    void whenSaveDocChunks_thenDocumentsAddedToVectorStore() {
        ParagraphPdfDocumentReader paragraphPdfDocumentReader = new ParagraphPdfDocumentReader(filePath);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        List<Document> documentChunks = tokenTextSplitter.apply(paragraphPdfDocumentReader.read());
        assertThat(documentChunks).isNotEmpty();
        assertThat(documentChunks.size()).isGreaterThan(0);

        vectorStore.add(documentChunks);
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "What is Inversion of Control in Spring Framework?",
        "What happens in Autumn season?"
    })
    @Order(2)
    void whenSearch_thenRelevantDocumentsReturned(String userQuery) {
        Message userMessage = new UserMessage(userQuery);

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("""

            You are a helpful assistant for answering questions related to Spring Framework.
            You will answer the questions strictly based on the context provided to you:
            {context}
            If you don't know the answer, say you don't know.

        """);

        List<Document> relevantDocuments = vectorStore.similaritySearch(userQuery); 

        assertThat(relevantDocuments).isNotEmpty().hasSizeGreaterThan(0);

        String contextContent = relevantDocuments.stream()
                .map(Document::getText)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
                   
        Message systemMessage = systemPromptTemplate
                .createMessage(Map.of("context", contextContent));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = azureOpenAiChatModel.call(prompt);

        assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
        
        logger.info("User query: {}", userQuery);
        logger.info("Chat Response: {}", chatResponse.getResult().getOutput().getText());
    }
}
