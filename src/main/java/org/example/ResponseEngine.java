package org.example;
import java.io.*;
import java.util.HashMap;
import java.awt.Desktop;
import java.util.Map;

public class ResponseEngine extends AbstractMemory {

    public ResponseEngine() {
        super("learned.txt");
//        "src/main/resources/learned.txt")
    }

    public String getResponse(String input) {
        String cleanedInput = input.trim().toLowerCase();


        if (cleanedInput.startsWith("learn:")) {
            String content = cleanedInput.substring(6).trim();
            String[] parts = content.split("\\?", 2);

            if (parts.length == 2) {
                String question = parts[0].trim() + "?";
                String answer = parts[1].trim();

                learnedResponse.put(question, answer);
                saveLearnedData(question, answer);
                overwriteLearnedFile();

                return "Got it! I've learned how to answer: \"" + question + "\"";
            } else {
                return "Hmm.. use the format: learn: your question? your answer";
            }
        }

        if (cleanedInput.startsWith("forget:")) {
            String question = cleanedInput.substring(7).trim();
            if (!question.endsWith("?")) {
                question += "?";
            }
            if (learnedResponse.containsKey(question)) {
                learnedResponse.remove(question);
                overwriteLearnedFile();
                return "Okay, I've forgotten how to answer \"" + question + "\"";
            } else {
                return "I don't remember learning that one.";
            }
        }

        if (cleanedInput.equals("list learned")) {
            if (learnedResponse.isEmpty()) {
                return "I haven't learned anything yet!";
            }

            StringBuilder response = new StringBuilder("Here's what I know:\n");
            for (String question : learnedResponse.keySet()) {

                response.append(" - ").append(question).append("\n");
            }
            return response.toString();
        }
        if (cleanedInput.equals("list full")) {
            if (learnedResponse.isEmpty()) {
                return "I haven't learned anything yet!";
            }

            StringBuilder response = new StringBuilder("Here's everything I know:\n");
            for (String question : learnedResponse.keySet()) {
                String answer = learnedResponse.get(question);
                response.append(" - ").append(question).append(" | ").append(answer).append("\n");
            }
            return response.toString();
        }
        if (cleanedInput.equals("export memory")) {
            if (learnedResponse.isEmpty()) {
                return "Nothing to export - I haven't learned anything yet!";
            }

            File exportFile = new File(getResourcePath("memory_export.txt"));

            try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
                for (String question : learnedResponse.keySet()) {
                    String answer = learnedResponse.get(question);
                    writer.println(question + "|" + answer
                    );
                }
                return "Memory exported to memory_export.txt successfully!";
            } catch (IOException e) {
                return "Something went wrong while exporting memory: " + e.getMessage();
            }
        }

        if (cleanedInput.equals("import memory")) {
            File importFile = new File(getResourcePath("memory_export.txt"));

            if (!importFile.exists()) {
                return "No memory file found to import.";
            }

            int importedCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(importFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        String question = parts[0].trim();
                        String answer = parts[1].trim();
                        if (!learnedResponse.containsKey(question)) {
                            learnedResponse.put(question, answer);
                            importedCount++;
                        }
                    }
                }
                overwriteLearnedFile();
                return "Imported " + importedCount + " new memory items from memory_export.txt.";
            } catch (IOException e) {
                return "Something went wrong while importing: " + e.getMessage();
            }
        }

        if (cleanedInput.startsWith("import")) {
            String fileName = cleanedInput.substring(7).trim();

            if (!fileName.endsWith(".txt")) {
                return "Please provide a valid .txt file to import!";
            }

            File imortFile = new File(getResourcePath(fileName));

            if (!imortFile.exists()) {
                return "Sorry, I can't find \"" + fileName + "\" in /resources.";
            }

            int importedCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(imortFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        String question = parts[0].trim();
                        String answer = parts[1].trim();
                        if (!learnedResponse.containsKey(question)) {
                            learnedResponse.put(question, answer);
                            importedCount++;
                        }
                    }
                }
                overwriteLearnedFile();
                return "Imported " + importedCount + " new memory items from " + fileName;
            } catch (IOException e) {
                return "Something went wrong while importing from " + fileName + ": " + e.getMessage();
            }
        }

        if (cleanedInput.equals("clear memory")) {
            learnedResponse.clear();

            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
                writer.print("");
            } catch (IOException e) {
                return "Error clearing memory: " + e.getMessage();
            }

            return "All memory has been cleared.";
        }

        String mathPattern = "(\\d+)\\s*([+\\-*/])\\s*(\\d+)";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(mathPattern).matcher(cleanedInput);

        if (matcher.find()) {
            try {
                String expression = matcher.group(1) + matcher.group(2) + matcher.group(3);
                int result = evalSimpleExpression(expression);
                return "The answer is: " + result;
            } catch (Exception e) {
                System.out.println("Failed to evaluate math: " + e.getMessage());
            }
        }

        for (String key : learnedResponse.keySet()) {
            if (isSimilar(cleanedInput, key)) {
                return learnedResponse.get(key);
            }
        }
        if (cleanedInput.contains("hi") || cleanedInput.contains("hello")) {
            return "Hey there! How can I help you?";
        } else if (cleanedInput.contains("how are you")) {
            return "I'm doing well — just thinking in binary!";
        } else if (cleanedInput.contains("what is your name")) {
            return "My name is Automis the AI.";
        } else if (cleanedInput.contains("favorite color")) {
            System.out.println("CLEANED INPUT: " + cleanedInput);
            return "My creator says my favorite color is blue.";
        } else if (cleanedInput.contains("what time is it")) {
            return java.time.LocalTime.now().toString();
        } else if (cleanedInput.contains("what day is it")) {
            return java.time.LocalDate.now().toString();
        } else {
            String launchResponse = handleAppLaunch(input);
            if (launchResponse != null) {
                return launchResponse;
            }

            String aiResponse = GroqApi.ask(input);

            if (aiResponse == null || aiResponse.toLowerCase().startsWith("error")) {
                return "Hmm... I'm having trouble reaching my AI brain right now.";
            }
            String trimmedInput = input.trim();
            if (!learnedResponse.containsKey(trimmedInput)) {
                learnedResponse.put(trimmedInput, aiResponse);
                saveLearnedData(trimmedInput, aiResponse);
                overwriteLearnedFile();
            }
            return aiResponse;


        }
    }
}
