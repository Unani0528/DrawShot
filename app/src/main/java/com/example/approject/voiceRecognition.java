package com.example.approject;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class voiceRecognition {
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private Context context;
    public voiceRecognition(Context context, RecognitionListener listener){
        this.context = context;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(listener);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());

    }
    public void startListening() {
        speechRecognizer.startListening(recognizerIntent);
    }

    public void stopListening() {
        speechRecognizer.stopListening();
    }

    public void destroy() {
        speechRecognizer.destroy();
    }
}
