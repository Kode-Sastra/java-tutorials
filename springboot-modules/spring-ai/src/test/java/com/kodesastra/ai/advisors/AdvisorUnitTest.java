package com.kodesastra.ai.advisors;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kodesastra.ai.advisors.entity.User;

@SpringBootTest
@ActiveProfiles("sa")
public class AdvisorUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorUnitTest.class);

    @Autowired
    ChatModel chatModel;

    @Test
    void whenConfigureChatClientWithAdvisors_thenInterceptLLMCallsWithAdvisors() {
        ChatClient chatClient = ChatClient.create(chatModel);

        User currentUser = new User("7012", "James");

        ChatResponse chatResponse = chatClient.prompt()
            .user("What is LLM?")
            .advisors(advisorSpec -> advisorSpec.advisors(
                    List.of(new LlmUsageLimitAdvisor(0),
                        new CacheLlmResponseAdvisor(1)))
                .param("user", currentUser))
            .call()
            .chatResponse();
        LOGGER.info("chatResponse: {}", chatResponse.getResult()
            .getOutput()
            .getContent());
    }

}
