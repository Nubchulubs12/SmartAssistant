package org.example;
import java.sql.SQLOutput;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ResponseEngine responder = new ResponseEngine();

        System.out.println("Java AI Assistant: Hello! Ask me anything (type 'exit' to quit)");

        while (true) {
            System.out.println("You: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Java AI Assistant : Goodbye!");
                break;
            }

            String response = responder.getResponse(input);
            System.out.println("Java AI Assistant: " + response);
        }
        scanner.close();
    }
}