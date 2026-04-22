package com.example.btn_duolingo;

import android.util.Log;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String API_KEY = "AIzaSyD2qHhl3BgTLH2OtD3FZAGvwkYrdikcI7M";
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();
    
    private static List<Exercise> cachedExercises = new ArrayList<>();

    public interface GeminiCallback {
        void onSuccess(List<Exercise> exercises);
        void onError(Throwable t);
    }

    public GeminiService() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.1f;
        GenerationConfig config = configBuilder.build();
        
        GenerativeModel gm = new GenerativeModel(
            "gemini-2.5-pro",
            API_KEY,
            config
        );
        
        model = GenerativeModelFutures.from(gm);
    }

    public void generateExercises(String language, String lessonTitle, GeminiCallback callback) {
        String prompt = "Create 10 exercises for learning " + language + " focusing on the topic: " + lessonTitle + ". " +
                "The output MUST be a JSON array. Each object in the array must have these fields: " +
                "id (int), title (string), description (string), " +
                "question (string, translate English to " + (language.equals("Chinese") ? "Chinese" : "Vietnamese") + "), " +
                "options (string, 6-8 words/punctuation marks separated by '|'), " +
                "answer (string, the correct translation). " +
                "Output ONLY raw JSON starting with [ and ending with ].";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText == null || resultText.isEmpty()) {
                    callback.onError(new Exception("AI returned empty text."));
                    return;
                }
                try {
                    String cleanJson = resultText.trim();
                    if (cleanJson.contains("[")) {
                        cleanJson = cleanJson.substring(cleanJson.indexOf("["), cleanJson.lastIndexOf("]") + 1);
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Exercise>>(){}.getType();
                    List<Exercise> exercises = gson.fromJson(cleanJson, listType);
                    if (exercises != null) {
                        cachedExercises = new ArrayList<>(exercises);
                        Collections.shuffle(cachedExercises);
                        callback.onSuccess(cachedExercises);
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        }, executor);
    }
}
