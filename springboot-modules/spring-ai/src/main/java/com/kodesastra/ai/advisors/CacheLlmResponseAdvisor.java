package com.kodesastra.ai.advisors;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

import com.kodesastra.ai.advisors.entity.User;

public class CacheLlmResponseAdvisor implements CallAroundAdvisor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheLlmResponseAdvisor.class);

    private int order;
    public CacheLlmResponseAdvisor(int order) {
        this.order = order;
    }
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        LOGGER.info("CacheLlmResponseAdvisor: aroundCall");
        AdvisedResponse response = null;
        Map<String, Object> context = advisedRequest.adviseContext();
        User user = (User) context.get("user");

        String userQuery = advisedRequest.userText();
        response = getResponseFromCache(user, userQuery);
        if(response != null) {
            LOGGER.info("Response from cache: {}", response.response()
                .getResult()
                .getOutput()
                .getContent());
            return response;
        } else {
            response = chain.nextAroundCall(advisedRequest);
            LOGGER.info("Response from LLM: {}", response.response()
                .getResult()
                .getOutput()
                .getContent());
            cacheResponse(user, response);
            return response;
        }
    }

    private void cacheResponse(User user, AdvisedResponse response) {
        // cache the response
        LOGGER.info("Caching the response for user: {}", user.getUserName());
    }

    private AdvisedResponse getResponseFromCache(User user, String userQuery) {
        // get the response from cache
        LOGGER.info("Getting the response from cache for user: {}", user.getUserName());

        return null;
    }

    @Override
    public String getName() {
        return "CacheLlmResponseAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }
}
