package com.ks.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

class McpToolsIntegrationTest {

	final Logger logger = LoggerFactory.getLogger(McpToolsIntegrationTest.class);

    @Autowired
	private ChatModel chatClient;

    @Test
    void whenNginxServiceDownThenRestartService() {
        String action = """
            Event:
            	Nginx service on node 'staging-api-01' is unresponsive.

            Action:
            	1. Check if the node is reachable
            	2. If the node is in Available state Restart the Nginx service on the node
            	3. Send a notification with the final status of the node to node operators

            Constraint:
            	Restart the service not more than two times.
            	""";
        String systemMessageText = """
            **MANDATORY PROTOCOL:**
            	1. NO recommendations, analysis, or speculation
            	2. Strictly use the tools available
            	3. Respond with EXACT tool output + status summary
            """;

        Message systemMessage = new SystemMessage(systemMessageText);
        Message userMessage = new UserMessage(action);

        String response = chatClient.call(systemMessage, userMessage);

        logger.info("Restart Node request Response: {}", response);

        assertThat(response)
            .contains("staging-api-01")
            .contains("restart");
    }

}
