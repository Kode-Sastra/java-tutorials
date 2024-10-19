package com.kodesastra.spring.qdrant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("manual")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpringQdrantManualConfigLiveTest {

    private static final Logger logger = LoggerFactory.getLogger(SpringQdrantManualConfigLiveTest.class);

    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private VectorStore qdrantVectorStore;

    @Value("${qdrant.collection-name}")
    private String collectionName;
    @Value("classpath:/book/meditation.pdf")
    private String pdfResource;

    @BeforeAll
    void setup() throws ExecutionException, InterruptedException {
        assertEquals("books", collectionName);
        createCollection(collectionName);
    }

    private void createCollection(String collectionName) throws ExecutionException, InterruptedException {
        qdrantClient.createCollectionAsync(collectionName, Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(1536)
                .build())
            .get();
    }

    private List<String> getIds(String query, String filter) {
        List<Document> resultDocuments = qdrantVectorStore.similaritySearch(SearchRequest.defaults()
            .withQuery(query)
            .withTopK(2)
            .withFilterExpression(filter)
            .withSimilarityThreshold(0.5));
        return resultDocuments.stream()
            .map(Document::getId)
            .toList();
    }

    @AfterAll
    void cleanup() throws ExecutionException, InterruptedException {
        //comment this out, in case you want to check the data on Qdrant DB after executing the tests.
        qdrantClient.deleteCollectionAsync(collectionName).get();
        qdrantClient.close();
    }

    @Test
    @Order(1)
    void givenQdrantDB_whenDisabledAutoConfig_thenManuallyConfigureVectorStore() {
        assertNotNull(qdrantVectorStore);
        assertInstanceOf(QdrantVectorStore.class, qdrantVectorStore);
    }

    @Test
    @Order(2)
    void givenQdrantDB_whenCallAddDocumentOfVectorStore_thenInsertSuccessfully()
        throws ExecutionException, InterruptedException {
        // Create documents related to meditation, In Real world application an ETL takes care of this
        Document doc1 = new Document("Mindfulness meditation helps you focus on the present moment.",
            Map.of("Author", "Shri Shri"));
        Document doc2 = new Document("Transcendental meditation involves repeating a mantra.",
            Map.of("Author", "Maharishi Yogi"));
        Document doc3 = new Document("Loving-kindness meditation fosters compassion and kindness.",
            Map.of("Author", "Sadguru"));
        Document doc4 = new Document("Zen meditation emphasizes posture and breathing.",
            Map.of("Author", "Katsuki Sekida"));
        Document doc5 = new Document("If the mind goes for a spin, then life goes for a toss",
            Map.of("Author", "Shri Shri"));
        // Add documents to a list
        List<Document> documents = Arrays.asList(doc1, doc2, doc3, doc4, doc5);

        assertEquals(0, qdrantClient.countAsync(collectionName)
            .get()
            .intValue());

        qdrantVectorStore.add(documents);

        assertTrue(qdrantClient.countAsync(collectionName).get().intValue() > 0);
        logger.info("Number of points in the {}: {}", collectionName,
            qdrantClient.countAsync(collectionName).get().intValue());
    }

    @Test
    @Order(3)
    void givenQdrantDB_whenCallSimilaritySearchOfVectorStore_thenSearchSuccessfully() {
        List<Document> resultDocuments = qdrantVectorStore.similaritySearch(SearchRequest.defaults()
            .withQuery("Mindfulness meditation")
            .withTopK(2)
            .withFilterExpression("Author == 'Shri Shri'")
            .withSimilarityThreshold(0.5));
        assertFalse(resultDocuments.isEmpty());
        resultDocuments.forEach(e -> assertEquals("Shri Shri", e.getMetadata().get("Author")));
    }
    @Test
    @Order(4)
    void givenQdrantDB_whenCallDeleteOfVectorStore_thenDeleteSuccessfully() {
        List<String> ids = getIds("Mindfulness meditation", "Author == 'Shri Shri'");
        qdrantVectorStore.delete(ids);
        assertEquals(0, getIds("Mindfulness meditation", "Author == 'Shri Shri'").size());
    }
}
