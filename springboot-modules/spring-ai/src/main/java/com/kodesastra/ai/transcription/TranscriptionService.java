package com.kodesastra.ai.transcription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TranscriptionService {
    private final Logger logger = LoggerFactory.getLogger(TranscriptionService.class);

    @Autowired
    private Model<AudioTranscriptionPrompt, AudioTranscriptionResponse> openAiAudioTranscriptionModel;

    public String transcribe(String audioFile) {
        Resource resource = new ClassPathResource(audioFile);
        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(resource);
        AudioTranscriptionResponse audioTranscriptionResponse = openAiAudioTranscriptionModel.call(audioTranscriptionPrompt);
        return audioTranscriptionResponse.getResult().getOutput();
    }
}
