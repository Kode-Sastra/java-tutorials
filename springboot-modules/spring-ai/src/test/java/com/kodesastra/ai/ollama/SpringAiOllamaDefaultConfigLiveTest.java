package com.kodesastra.ai.ollama;

import java.io.IOException;


import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;

@SpringBootTest
@Import(OllamaTestContainersDefaultConfig.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("ollama")
class SpringAiOllamaDefaultConfigLiveTest {
	private final Logger logger = LoggerFactory.getLogger(SpringAiOllamaDefaultConfigLiveTest.class);

	@Autowired
	private OllamaContainer ollamaContainer;

	@Autowired
	private OllamaChatModel ollamaChatModel;

	@BeforeAll
	public void setup() throws IOException, InterruptedException {
		//print the Ollama URL and port
		logger.info("Ollama URL: {}, port: {}", ollamaContainer.getEndpoint(), ollamaContainer.getPort());
		Container.ExecResult execResult = ollamaContainer.execInContainer("ollama", "pull",
			OllamaModel.LLAMA3_2.getName());
		if (execResult.getExitCode() != 0) {
			logger.error("Failed to pull model: {}", execResult.getStderr());
			throw new IOException("Failed to pull model: " + execResult.getStderr());
		}
	}

	@Test
	void givenDefaultOllamaConnection_whenInvokedWithPrompt_thenResponds() {
		logger.info("SpringAIOllamaLiveTest context loaded successfully.");
		String prompt = """
			Context:
			The Amazon rainforest is the largest tropical rainforest in the world, spanning several countries in South America.
			It is home to a vast diversity of plant and animal species, many of which are not found anywhere else on Earth.
			The rainforest plays a crucial role in regulating the global climate by absorbing large amounts of carbon dioxide.
			Question: Why is the Amazon rainforest important for the Earth's climate?
			Instructions:
			Please answer strictly from the context provided in the prompt and do not include any additional information.
			Keep the answer short and concise.
			""";

		ChatResponse response = ollamaChatModel.call(new Prompt(prompt, OllamaOptions.builder()
			.model(OllamaModel.LLAMA3_2)
			.temperature(0.4)
			.build()));
		assertThat(response.getResult()
			.getOutput()).isNotNull()
			.extracting(output -> output.getText().toLowerCase())
			.asString()
			.contains("carbon dioxide");

		logger.info("Response: {}", response.getResult()
			.getOutput()
			.getText());
	}
}
