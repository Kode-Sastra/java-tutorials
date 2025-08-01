package com.kodesastra.ai.ollama;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Profile("ollama")
class OllamaTestContainersDefaultConfig {
    @Bean
    @ServiceConnection
    OllamaContainer ollamaContainer() {
        return new OllamaContainer(
			DockerImageName.parse("ollama/ollama:latest")
        ).withCreateContainerCmdModifier(cmd ->
	        cmd.getHostConfig().withDeviceRequests(null)
        );
    }
}
