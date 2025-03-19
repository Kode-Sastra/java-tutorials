package com.kodesastra.ai.transcription;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.model.Model;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tr")
public class TranscriptionAILiveTest {
    private final Logger logger = LoggerFactory.getLogger(TranscriptionAILiveTest.class);

    @Autowired
    private Model<AudioTranscriptionPrompt, AudioTranscriptionResponse> aiModel;

    @Test
    void whenOpenAIConfigsDefinedInPropFile_thenTranscribe() {
        assertInstanceOf(OpenAiAudioTranscriptionModel.class, aiModel);

        String audioFile = "/audio/song.mp3";
        Resource resource = new ClassPathResource(audioFile);
        OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel
            = OpenAiAudioTranscriptionModel.class.cast(aiModel);
        String transcribedText = openAiAudioTranscriptionModel.call(resource);
        logger.info("The transcribed text: {}", transcribedText);
        assertTrue(transcribedText.toUpperCase().contains("LIKE A DIAMOND IN THE SKY"));
    }

    @Test
    void whenOpenAIConfigsDefinedProgrammatically_thenTranscribe() {
        OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel
            = OpenAiAudioTranscriptionModel.class.cast(aiModel);

        String audioFile = "/audio/song.mp3";
        Resource resource = new ClassPathResource(audioFile);

        OpenAiAudioTranscriptionOptions transcriptionOptions
            = OpenAiAudioTranscriptionOptions.builder()
            .language("en")
            .model("whisper-1")
            .temperature(0.5f)
            .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.VTT)
            .build();
        AudioTranscriptionPrompt audioTranscriptionPrompt
            = new AudioTranscriptionPrompt(resource, transcriptionOptions);

        AudioTranscriptionResponse audioTranscriptionResponse
            = openAiAudioTranscriptionModel.call(audioTranscriptionPrompt);

        String transcribedText = audioTranscriptionResponse.getResult().getOutput();
        logger.info("The transcribed text: {}", transcribedText);
        assertTrue(transcribedText.toUpperCase().contains("LIKE A DIAMOND IN THE SKY"));
    }
}
