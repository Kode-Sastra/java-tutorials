package com.ks.azureopenai;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;

public class VectorDBService {

    private SimpleVectorStore vectorStore;

    public VectorDBService(SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveDocs(List<Document> docs) {
        this.vectorStore.doAdd(docs);
    }

    public String fetchContextFromVectorDB(String query) {
        List<Document> queryResults = vectorStore.doSimilaritySearch(
            SearchRequest.builder()
                .query(query).topK(2)
                .build()
        );
        StringBuilder contextBuilder = new StringBuilder();
        for (Document docs : queryResults) {
            contextBuilder.append(docs.getText()).append(" ");
        }
        return contextBuilder.toString().trim();
    }

}
