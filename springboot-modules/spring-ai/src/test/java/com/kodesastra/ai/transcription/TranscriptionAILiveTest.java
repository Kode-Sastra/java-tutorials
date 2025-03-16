package com.kodesastra.ai.transcription;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tr")
public class TranscriptionAILiveTest {
    private final Logger logger = LoggerFactory.getLogger(TranscriptionAILiveTest.class);

    @Autowired
    private TranscriptionService transcriptionService;
    @Test
    void whenOpenAIConfigsDefinedInPropFile_thenTranscribe() {
        logger.info("Test");
        String transcription = transcriptionService.transcribe("/audio/song.m4a");
        logger.info("Transcription: {}", transcription);
    }
}
