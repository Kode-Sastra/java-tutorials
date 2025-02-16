package com.kodesastra.spring.qdrant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

@Configuration
@Profile("manual")
public class ApplicationConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Value("${qdrant.host}")
    private String qdrantHost;
    @Value("${qdrant.port}")
    private int qdrantPort;
    @Value("${qdrant.api-key}")
    private String qdrantAPIKey;
    @Value("${qdrant.collection-name}")
    private String collection;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(getQdrantHost(), getQdrantPort(), true);
        builder.withApiKey(getQdrantAPIKey());
        QdrantGrpcClient qdrantGrpcClient = builder.build();
        return new QdrantClient(qdrantGrpcClient);
    }

    @Bean
    public QdrantVectorStore qdrantVectorStore(EmbeddingModel embeddingModel, QdrantClient qdrantClient) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
            .collectionName(collection)
            .build();
    }

    private String getQdrantHost() {
        logger.info("Qdrant host: {}", qdrantHost);
        //call a downstream service to fetch host
        return qdrantHost;
    }

    private int getQdrantPort() {
        logger.info("Qdrant port: {}", qdrantPort);
        //call a downstream service to fetch port
        return qdrantPort;
    }

    private String getQdrantAPIKey() {
        //call a downstream service to fetch API Key
        return qdrantAPIKey;
    }
}
