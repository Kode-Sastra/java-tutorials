package com.kodesastra.spring.qdrant;

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

    @Value("${qdrant.host}")
    private String qdrantHost;
    @Value("${qdrant.port}")
    private int qdrantPort;
    @Value("${qdrant.api-key}")
    private String qdrantAPIKey;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(getQdrantHost(), getQdrantPort(), true);
        builder.withApiKey(getQdrantAPIKey());
        QdrantGrpcClient qdrantGrpcClient = builder.build();
        return new QdrantClient(qdrantGrpcClient);
    }

    @Bean
    public QdrantVectorStore qVectorStore(EmbeddingModel embeddingModel, QdrantClient qdrantClient) {
        return new QdrantVectorStore(qdrantClient, "books", embeddingModel, false);
    }

    private String getQdrantHost() {
        //call a downstream service to fetch host
        return qdrantHost;
    }

    private int getQdrantPort() {
        //call a downstream service to fetch port
        return qdrantPort;
    }

    private String getQdrantAPIKey() {
        //call a downstream service to fetch API Key
        return qdrantAPIKey;
    }
}
