package com.ks.azureopenai;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("azureopenai")
public class SpringAIAzureOpenAILiveTest {
    final Logger logger = LoggerFactory.getLogger(SpringAIAzureOpenAILiveTest.class);
    @Autowired
    private ChatService chatService;

    @Test
    void test() {
        logger.info("invoked successfully");
        String response = chatService.chat("what is the capital of India?");
        assertThat(response)
            .isNotNull()
            .containsIgnoringCase("New Delhi");
        logger.info("response: {}", response);
    }

}
