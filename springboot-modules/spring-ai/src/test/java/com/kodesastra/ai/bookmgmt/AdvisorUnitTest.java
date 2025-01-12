package com.kodesastra.ai.bookmgmt;

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

import com.kodesastra.ai.advisors.CacheLlmResponseAdvisor;
import com.kodesastra.ai.advisors.LlmUsageLimitAdvisor;
import com.kodesastra.ai.advisors.entity.User;

@SpringBootTest
@ActiveProfiles("sa")
public class AdvisorUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorUnitTest.class);

    @Autowired
    ChatModel chatModel;

    @Test
    void test() {
        LOGGER.info("AdvisorUnitTest: test");
        // or create a ChatClient with the default builder settings:
        ChatClient chatClient = ChatClient.create(chatModel);
        //set the User object in the AdviseContext
        User currentUser = new User("7012", "Parthiv Pradhan");

        ChatResponse chatResponse = chatClient.prompt()
            .user("Tell me a small Joke")
            .advisors( advisorSpec -> advisorSpec
                .advisors(List.of(new CacheLlmResponseAdvisor(0), new LlmUsageLimitAdvisor(1)))
                .param("user", currentUser))
            .call()
            .chatResponse();
        LOGGER.info("chatResponse: {}", chatResponse.getResult()
            .getOutput()
            .getContent());

    }

}
