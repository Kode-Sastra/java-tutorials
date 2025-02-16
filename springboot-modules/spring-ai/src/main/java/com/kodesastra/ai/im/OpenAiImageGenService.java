package com.kodesastra.ai.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAiImageGenService {
    private final Logger logger = LoggerFactory.getLogger(OpenAiImageGenService.class);

    @Autowired
    private ImageModel openAIImageModel;

    public String generateImage(String instructions) {
        ImagePrompt prompt = new ImagePrompt(instructions);
        ImageResponse imageResponse = openAIImageModel.call(prompt);
        return imageResponse.getResult().getOutput().getUrl();
    }

    public String generateImage(String instruction, int height, int width, String model) {
        ImageOptions imageOptions = ImageOptionsBuilder.builder()
            .height(height)
            .width(width)
            .model(model)
            .build();
        ImagePrompt prompt = new ImagePrompt(instruction, imageOptions);
        ImageResponse imageResponse = openAIImageModel.call(prompt);
        return imageResponse.getResult().getOutput().getUrl();
    }
}
