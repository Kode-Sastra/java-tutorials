package com.kodesastra.spring.rag;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("rag")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RagTechniqueLiveTest {

    private static final Logger logger = LoggerFactory.getLogger(RagTechniqueLiveTest.class);

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private QdrantClient qdrantClient;
    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collection;
    @Value("classpath:/rag/user_query_prompt.st")
    private Resource systemResource;
    @Autowired
    private ChatModel chatModel;

    @Value("classpath:/rag/meditation_and_its_methods.pdf")
    private String filePath;

    private List<Document> documentChunks;

    @BeforeAll
    void setup() throws ExecutionException, InterruptedException {
        assertEquals("books", collection);
        createCollection(collection);
    }

    private void createCollection(String collectionName) throws ExecutionException, InterruptedException {
        qdrantClient.createCollectionAsync(
            collectionName, Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(1536)
                .build())
            .get();
    }

    @AfterAll
    void cleanup() throws ExecutionException, InterruptedException {
        //comment this out, in case you want to check the data on Qdrant DB after executing the tests.
        qdrantClient.deleteCollectionAsync(collection).get();
        qdrantClient.close();
    }

    @Test
    @Order(1)
    void whenReadPdf_thenGenerateDocumentChunks() {
        ParagraphPdfDocumentReader paragraphPdfDocumentReader
            = new ParagraphPdfDocumentReader(filePath);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> documents = paragraphPdfDocumentReader.get();
        logger.info("number of original documents: {}", documents.size());
        documentChunks = tokenTextSplitter.apply(documents);
        assertTrue(documentChunks.size() > 1);
        logger.info("number of documents chunks after splitting: {}", documentChunks.size());
    }

    @Test
    @Order(2)
    void whenDocumentChunksGenerated_thenSaveInVectorDB() {
        assertInstanceOf(QdrantVectorStore.class, vectorStore);
        assertDoesNotThrow(() -> vectorStore.add(documentChunks));
    }

    @ParameterizedTest
    @Order(3)
    @ValueSource(strings = {"What are the benefits of meditation?",
        "What are the benefits of Tibetan meditation?"})
    void whenQueryDocument_thenRespondWithInContext(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(systemResource);
        promptTemplate.add("context", vectorStore.similaritySearch(query));
        promptTemplate.add("query", query);
        String prompt = promptTemplate.render();

        String chatResponse = chatModel.call(prompt);
        logger.info("Chat bot response: {}", chatResponse);
    }
}
