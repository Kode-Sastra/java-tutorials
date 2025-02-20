package com.kodesastra.ai.im;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("im")
public class OpenAiImageGenLiveTest {

    private final Logger logger = LoggerFactory.getLogger(OpenAiImageGenLiveTest.class);
    @Autowired
    private OpenAiImageGenService openAIImageGenService;

    @Test
    void whenInvokeOpenAIImageModel_thenReturnImageUrl() {
        String instruction = "A humming bird fluttering above a small girl's head";
        String imageUrl = openAIImageGenService.generateImage(instruction);

        logger.info("imageUrl: {}", imageUrl);
    }

    @Test
    void whenInvokeOpenAIImageModelWithImageAttributes_thenReturnImageUrl() {
        String instruction = "A humming bird fluttering above a small girl's head";
        String imageUrl = openAIImageGenService.generateImage(instruction, 256, 256, "dall-e-2");

        logger.info("imageUrl: {}", imageUrl);
    }
}
