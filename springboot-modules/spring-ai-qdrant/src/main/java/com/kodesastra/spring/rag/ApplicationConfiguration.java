package com.kodesastra.spring.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
@Configuration
@Profile("rag")
public class ApplicationConfiguration {
    @Value("${spring.ai.vectorstore.qdrant.host}")
    private String qdrantHost;
    @Value("${spring.ai.vectorstore.qdrant.port}")
    private int qdrantPort;
    @Value("${spring.ai.vectorstore.qdrant.api-key}")
    private String qdrantAPIKey;
    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collection;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(qdrantHost, qdrantPort, true);
        builder.withApiKey(qdrantAPIKey);
        QdrantGrpcClient qdrantGrpcClient = builder.build();
        return new QdrantClient(qdrantGrpcClient);
    }

}
