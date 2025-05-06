package org.example;
import java.awt.Desktop;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AbstractMemory {
    protected final HashMap<String, String> learnedResponse = new HashMap<>();
    protected final String FILE_PATH;
    private File currentDirectory = new File(System.getProperty("user.dir"));

    public AbstractMemory(String filePath) {
        this.FILE_PATH = filePath;
        loadLearnedData();
    }

    protected String listDirectory() {
        File[] files = currentDirectory.listFiles();
        if(files == null || files.length ==0) {
            return "Directory is empty or inaccessible.";
        }
        StringBuilder response = new StringBuilder("Contents of " + currentDirectory.getAbsolutePath() + ":\n");
        for (File file : files) {
            response.append(file.isDirectory() ? "[Dir] " : "[File] ")
                    .append(file.getName())
                    .append("\n");
        }
        return response.toString();
    }
    protected String changeDirectory(String path) {
        File newDir;
        if (path.equals("..")) {
            newDir = currentDirectory.getParentFile();
        } else {
            newDir = new File(currentDirectory, path);
        }

        if (newDir == null || !newDir.exists() || !newDir.isDirectory()) {
            return "Cannot navigate to '" + path + "':Directory does not exist.";
        }
        return "Changed to: " + currentDirectory.getAbsolutePath();
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
    protected String executeSystemCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command, null, currentDirectory);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Command failed with exit code " + exitCode + ":\n" + output.toString();
            }
            return output.toString();
        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }
    }
    protected String openFile(String fileName) {
        File file = new File(currentDirectory, fileName);
        if (!file.exists() || file.isDirectory()) {
            return "File '" + fileName + "' does not exist or is a directory.";
        }

        try {
            Desktop.getDesktop().open(file);
            return "Opening '" + fileName + "'...";
        } catch (IOException e) {
            return "Failed to open '" + fileName + "': " + e.getMessage();
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
//"src/main/resources/" +
    protected String getResourcePath(String fileName) {
        return fileName;
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
//for desktop maninpulation
    protected String handleAppLaunch(String input) {
        String cleanedInput = input.toLowerCase().trim();
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean isMac = os.contains("mac");
        boolean isLinux = os.contains("nux") || os.contains("nix");

        Map<String, String> appCommands = new HashMap<>();
        Map<String, String> fallbackCommands = new HashMap<>();
        if (isWindows) {
            appCommands.put("notepad", "notepad");
            appCommands.put("chrome", "cmd /c start chrome");
            appCommands.put("calculator", "calc");

            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            appCommands.put("chrome", "cmd /c start \"\" \"" + chromePath + "\"");
            fallbackCommands.put("chrome", null);
        }  else if (isMac) {
        appCommands.put("notepad", "open -a TextEdit");
        appCommands.put("chrome", "open -a \"Google Chrome\"");
        appCommands.put("calculator", "open -a Calculator");
        appCommands.put("explorer", "open .");
        appCommands.put("cmd", "open -a Terminal");
    } else if (isLinux) {
        appCommands.put("notepad", "gedit");
        appCommands.put("chrome", "google-chrome");
        appCommands.put("calculator", "gnome-calculator");
        appCommands.put("explorer", "nautilus .");
        appCommands.put("cmd", "x-terminal-emulator");
    } else {
        return "Unsupported operating system: " + os;
    }

        for (String key : appCommands.keySet()) {
            if (cleanedInput.contains("open " + key)) {
                try {
                    String command = appCommands.get(key);
                    System.out.println("Attempting to execute: " + command);
                    Process process = Runtime.getRuntime().exec(command, null, currentDirectory);
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        System.out.println("Command failed with exit code: " + exitCode);
                        if (fallbackCommands.containsKey(key) && fallbackCommands.get(key) != null) {
                            command = fallbackCommands.get(key);
                            System.out.println("Trying fallback command: " + command);
                            process = Runtime.getRuntime().exec(command, null, currentDirectory);
                            exitCode = process.waitFor();
                            if (exitCode != 0) {
                                return "Could not open " + key + ": Fallback command failed with exit code " + exitCode;
                            }
                        } else {
                            return "Could not open " + key + ": Command failed with exit code " + exitCode;
                        }
                    }
                    return "Opening " + key + "...";
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error executing command: " + e.getMessage());
                    return "Could not open " + key + ": " + e.getMessage();
                }
            }
        }

        return null;
    }
    protected String getCurrentDirectory() {
        return "Current directory: " + currentDirectory.getAbsolutePath();
    }


}
