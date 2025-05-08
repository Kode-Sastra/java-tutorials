package com.kodesastra.ai.tts;

import java.io.IOException;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TtsService {
    @Value("${spring.ai.openai.audio.speech.api-key}")
    private String API_KEY;

    public byte[] textToSpeech(String text, String instruction) throws IOException {
        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
            .apiKey(API_KEY)
            .build();
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
            .model(OpenAiAudioApi.TtsModel.TTS_1_HD.value)
            .voice(OpenAiAudioApi.SpeechRequest.Voice.CORAL)
            .speed(0.75f)
            .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
            .input(text)
            .build();
        OpenAiAudioSpeechModel openAiAudioSpeechModel = new OpenAiAudioSpeechModel(openAiAudioApi, speechOptions);
        SpeechPrompt speechPrompt = new SpeechPrompt(instruction, speechOptions);
        return openAiAudioSpeechModel.call(speechPrompt).getResult().getOutput();
    }
}
