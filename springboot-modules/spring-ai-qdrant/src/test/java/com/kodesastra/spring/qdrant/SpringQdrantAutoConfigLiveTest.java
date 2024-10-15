package com.kodesastra.spring.qdrant;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("auto")
class SpringQdrantAutoConfigLiveTest {

	@Autowired
	private VectorStore qdrantVectorStore;

	@Test
	void givenQdrantDB_whenPropertiesDefined_thenAutoConfigureVectorStore() {
		assertNotNull(qdrantVectorStore);
		assertInstanceOf(QdrantVectorStore.class, qdrantVectorStore);
	}
}
