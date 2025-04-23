//package org.example;
//
//import okhttp3.*;
//import com.google.gson.*;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
//public class FireWorksApi {
//    private static final String API_URL = "https://api.fireworks.ai/inference/v1/chat/completions";
//    private static final String MODEL = "accounts/fireworks/models/mixtral-8x7b-instruct"; // or mistral-7b
//
//    private static final OkHttpClient client = new OkHttpClient.Builder()
//            .readTimeout(60, TimeUnit.SECONDS)
//            .build();
//
//    public static String ask(String prompt) {
//        JsonObject message = new JsonObject();
//        message.addProperty("role", "user");
//        message.addProperty("content", prompt);
//
//        JsonArray messages = new JsonArray();
//        messages.add(message);
//
//        JsonObject payload = new JsonObject();
//        payload.addProperty("model", MODEL);
//        payload.add("messages", messages);
//        payload.addProperty("temperature", 0.7);
//        payload.addProperty("max_tokens", 300);
//
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(RequestBody.create(payload.toString(), MediaType.parse("application/json")))
//                .addHeader("Authorization", "Bearer " + System.getenv("FIREWORKS_API_KEY"))
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) return "Error: " + response.code();
//            String body = response.body().string();
//            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
//            return json.getAsJsonArray("choices")
//                    .get(0).getAsJsonObject()
//                    .getAsJsonObject("message")
//                    .get("content").getAsString().trim();
//        } catch (IOException e) {
//            return "Fireworks request failed: " + e.getMessage();
//        }
//    }
//}

