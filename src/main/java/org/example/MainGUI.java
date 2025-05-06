package org.example;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private final ResponseEngine engine = new ResponseEngine(); // your AI logic class

    @Override
    public void start(Stage primaryStage) {
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(250);

        TextField inputField = new TextField();
        inputField.setPromptText("Ask me something...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(chatArea, inputField));
        inputField.setOnAction(e -> sendMessage(chatArea, inputField));

        CheckBox darkModeToggle = new CheckBox("Dark Mode");
        darkModeToggle.setSelected(true);

        HBox topBar = new HBox(darkModeToggle);
        topBar.setSpacing(10);
        topBar.setStyle("-fx-alignment: top-right; -fx-padding: 5 0 5 0;");

        HBox inputBar = new HBox(10, inputField, sendButton);
        inputBar.setStyle("-fx-alignment: center;");
        inputField.setPrefWidth(400);

        VBox inputContainer = new VBox(5, inputField, sendButton);
        inputContainer.setStyle("-fx-alignment: center;");

        VBox layout = new VBox(10, chatArea, inputContainer, topBar, inputBar);
        layout.setId("main-layout");


        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());

        darkModeToggle.setOnAction(e -> {
            scene.getStylesheets().clear();
            if (darkModeToggle.isSelected()) {
                scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/light.css").toExternalForm());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("AI Assistant");
        primaryStage.show();



    }
    private void sendMessage(TextArea chatArea, TextField inputField) {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        chatArea.appendText("You: " + userInput + "\n");

        // Run AI logic on a background thread
        Task<String> responseTask = new Task<>() {
            @Override
            protected String call() {
                return engine.getResponse(userInput);
            }
        };

        responseTask.setOnSucceeded(event -> {
            String response = responseTask.getValue();
            chatArea.appendText("AI: " + response + "\n\n");
            inputField.clear();
        });

        responseTask.setOnFailed(event -> {
            chatArea.appendText("AI: Sorry, something went wrong.\n\n");
        });

        new Thread(responseTask).start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

