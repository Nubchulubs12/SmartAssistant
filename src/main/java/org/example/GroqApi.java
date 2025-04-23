package org.example;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GroqApi {
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama3-8b-8192";
    // or llama3-8b-8192

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static String ask(String prompt) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);
        payload.add("messages", messages);
        payload.addProperty("max_tokens", 400);
        payload.addProperty("temperature", 0.5);





        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(payload.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + System.getenv("GROQ_API_KEY")) // Optional for Groq now
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return "Error: " + response.code();
            String body = response.body().string();
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString().trim();
        } catch (IOException e) {
            return "Groq request failed: " + e.getMessage();
        }
    }
}
