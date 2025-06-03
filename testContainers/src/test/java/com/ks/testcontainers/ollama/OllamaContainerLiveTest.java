package com.ks.testcontainers.ollama;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.ollama.OllamaContainer;

public class OllamaContainerLiveTest {
    private static Logger logger = LoggerFactory.getLogger(OllamaContainerLiveTest.class);

    static OllamaContainer ollamaContainer;
    private static final String OLLAMA_IMAGE = "ollama/ollama:latest";
    private static final String LLM = "tinyllama:1.1b";

    @BeforeAll
    static void init() throws IOException, InterruptedException {

        ollamaContainer = new OllamaContainer(OLLAMA_IMAGE);
        ollamaContainer.withCreateContainerCmdModifier((cmd) ->
            cmd.getHostConfig().withDeviceRequests(null));

        ollamaContainer.start();

        ollamaContainer.execInContainer("ollama", "pull", LLM);
    }

    @AfterAll
    static void cleanUp() {
        if (ollamaContainer != null) {
            try {
                ollamaContainer.stop();
            } catch (Exception e) {
                logger.error("Error stopping Ollama container: {}", e.getMessage());
            }
        }
    }

    @Test
    void test() throws IOException, InterruptedException {
        String prompt = """
            The sun is a star located at the center of our solar system.
            It provides the Earth with heat and light, making life possible.
            The sun is composed mostly of hydrogen and helium
            and generates energy through nuclear fusion."
            Question: What two gases make up most of the sun?
            Please answer strictly from the information provided.
            Keep the answer short and concise."
            """;
        Container.ExecResult execResult =
            ollamaContainer.execInContainer("ollama", "run", LLM, prompt);
        assertEquals(0, execResult.getExitCode(), "Exit code should be 0");
        logger.info("Exec Result: {}", execResult.getStdout());
    }
}