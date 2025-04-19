package org.example;
import java.io.*;
import java.util.HashMap;

public class ResponseEngine {
    private final HashMap<String, String> learnedResponse = new HashMap<>();
    private final String FILE_PATH = getResourcePath("learned.txt");

    public ResponseEngine() {
        loadLearnedData();
    }

    public String getResponse(String input) {
        String cleanedInput = input.trim().toLowerCase();

    if (cleanedInput.startsWith("learn:")) {
        String content = cleanedInput.substring(6).trim();
        String[] parts= content.split("\\?",2);

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

            try(PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
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

        if(cleanedInput.startsWith("import")) {
            String fileName = cleanedInput.substring(7).trim();

            if (!fileName.endsWith(".txt")) {
                return "Please provide a valid .txt file to import!";
            }

            File imortFile = new File(getResourcePath(fileName));

            if (!imortFile.exists()) {
                return "Sorry, I can't find \"" + fileName + "\" in /resources.";
            }

            int importedCount = 0;

            try(BufferedReader reader = new BufferedReader(new FileReader(imortFile))) {
                String line;
                while((line = reader.readLine()) !=null) {
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
            }catch (IOException e) {
                return "Something went wrong while importing from " + fileName + ": " + e.getMessage();
            }
        }

        if (cleanedInput.equals("clear memory")) {
            learnedResponse.clear();

            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {// Wipe learned.txt
            } catch (IOException e) {
                return "Error clearing memory: " + e.getMessage();
            }

            return "All memory has been cleared.";
        }


        for (String key : learnedResponse.keySet()) {
            if (isSimilar(cleanedInput, key)) {
                return learnedResponse.get(key);
            }
        }
        switch (cleanedInput) {
            case "hi":
            case "hello":
                return "Hey there! How can I help you?";
            case "how are you":
                return "I'm doing well — just thinking in binary!";
            case "what is your name":
                return "I'm your Java AI Assistant.";
            case "what time is it":
                return java.time.LocalTime.now().toString();
            case "what day is it":
                return java.time.LocalDate.now().toString();
            default:
                return "Hmm... I’m still learning! Try asking something else.";
        }
    }

    private void saveLearnedData(String question, String answer) {
        try(FileWriter fw = new FileWriter(FILE_PATH,true);
        BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(question + "|" + answer);
        } catch (IOException e) {
            System.out.println("Error saving learned data:" + e.getMessage());
        }
    }

    private void loadLearnedData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {

                    learnedResponse.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading learned data: " + e.getMessage());
        }
    }

    private void overwriteLearnedFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String question : learnedResponse.keySet()) {
                String answer = learnedResponse.get(question);
                writer.println(question + "|" + answer);
            }
        } catch (IOException e) {
            System.out.println("Error updating learned data: " + e.getMessage());
        }
    }

    private boolean isSimilar(String input, String storedKey) {
        String cleanInput = input.replaceAll("[^a-z0-9]", "").toLowerCase();
        String cleanKey = storedKey.replaceAll("[^a-z0-9]", "").toLowerCase();

        return cleanInput.equals(cleanKey)
                || cleanInput.contains(cleanKey)
                || cleanKey.contains(cleanInput);
    }

    private String getResourcePath(String fileName) {
        return "src/main/resources/" + fileName;
    }


}
