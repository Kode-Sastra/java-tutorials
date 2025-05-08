package com.kodesastra.ai.tts;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tts")
public class SpringAiTtsLiveTest {
    private final Logger logger = LoggerFactory.getLogger(SpringAiTtsLiveTest.class);

    @Autowired
    private SpeechModel openAiAudioSpeechModel;

    @Autowired
    private TtsService ttsService;

   @Test
    void givenAutoConfiguredOpenAiAudioSpeechModel_whenCalled_thenCreateAudioFile() {
        assertInstanceOf(OpenAiAudioSpeechModel.class, openAiAudioSpeechModel);
        byte[] audioBytes;
        try {
            audioBytes = openAiAudioSpeechModel
                .call(FileWriterUtil.readFile(Paths.get("poem.txt")));
            FileWriterUtil.writeFile(audioBytes, Paths.get("tts-output/twinkle-auto.mp3"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
   }

    @Test
    void givenManuallyConfiguredOpenAiAudioSpeechModel_whenCalled_thenCreateAudioFile() {
        byte[] audioBytes;
        try {
            final String instruction = "Read the poem with a calm and soothing voice.";
            audioBytes = ttsService
                .textToSpeech(FileWriterUtil.readFile(Paths.get("poem.txt")), instruction);

            FileWriterUtil.writeFile(audioBytes, Paths.get("tts-output/twinkle-manual.mp3"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
