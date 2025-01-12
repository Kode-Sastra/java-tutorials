package com.kodesastra.ai.advisors;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

import com.kodesastra.ai.advisors.entity.User;

public class LlmUsageLimitAdvisor implements CallAroundAdvisor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LlmUsageLimitAdvisor.class);

    private int order;
    public LlmUsageLimitAdvisor(int order) {
        this.order = order;
    }
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        LOGGER.info("LlmUsageLimitAdvisor: aroundCall");
        Map<String, Object> context = advisedRequest.adviseContext();
        User user = (User) context.get("user");

        if(isUserUsageWithInLimit(user)) {
            AdvisedResponse response = chain.nextAroundCall(advisedRequest);
            return response;
        } else {
            LOGGER.warn("User usage limit exceeded for user: {}", user.getUserName());
            throw new RuntimeException("User usage limit exceeded");

        }
    }

    private boolean isUserUsageWithInLimit(User user) {
        LOGGER.info("Checking user usage limit for user: {}", user.getUserName());
        return true;
    }

    @Override
    public String getName() {
        return "LlmUsageLimitAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }
}
