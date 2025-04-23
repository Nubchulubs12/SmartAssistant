package org.example;

import java.io.*;
import java.util.HashMap;

public class AbstractMemory {
    protected final HashMap<String, String> learnedResponse = new HashMap<>();
    protected final String FILE_PATH;

    public AbstractMemory(String filePath) {
        this.FILE_PATH = filePath;
        loadLearnedData();
    }
    protected void saveLearnedData(String question, String answer) {
        try(FileWriter fw = new FileWriter(FILE_PATH,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(question + "|" + answer);
        } catch (IOException e) {
            System.out.println("Error saving learned data:" + e.getMessage());
        }
    }

    protected void loadLearnedData() {
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

    protected void overwriteLearnedFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String question : learnedResponse.keySet()) {
                String answer = learnedResponse.get(question);
                writer.println(question + "|" + answer);
            }
        } catch (IOException e) {
            System.out.println("Error updating learned data: " + e.getMessage());
        }
    }

    protected boolean isSimilar(String input, String storedKey) {
        String cleanInput = input.replaceAll("[^a-z0-9]", "").toLowerCase();
        String cleanKey = storedKey.replaceAll("[^a-z0-9]", "").toLowerCase();

        return cleanInput.equals(cleanKey)
                || cleanInput.contains(cleanKey)
                || cleanKey.contains(cleanInput);
    }

    protected String getResourcePath(String fileName) {
        return "src/main/resources/" + fileName;
    }
    protected int evalSimpleExpression(String expr) {
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
        } else if (expr.contains("-")) {
            String[] parts = expr.split("-");
            return Integer.parseInt(parts[0]) - Integer.parseInt(parts[1]);
        } else if (expr.contains("*")) {
            String[] parts = expr.split("\\*");
            return Integer.parseInt(parts[0]) * Integer.parseInt(parts[1]);
        } else if (expr.contains("/")) {
            String[] parts = expr.split("/");
            return Integer.parseInt(parts[0]) / Integer.parseInt(parts[1]);
        }

        throw new IllegalArgumentException("Unsupported math format");
    }


}
