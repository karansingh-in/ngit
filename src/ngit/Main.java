package ngit;

import ngit.commands.*;
import ngit.repository.Repository;
import ngit.utils.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, DataFormatException {
        Repository repo = new Repository();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("\u001B[32mOn ngit$ "); //Green color
            System.out.println("\u001B[0m ");

            String text = sc.nextLine();
            List<String> words = tokenizer(text);
            if(words.isEmpty()){
                continue;
            }
            if (words.get(0).equals("ngit")){
                dispatch(words.toArray(new String[0]), repo);
            }
        }
    }

    public static void dispatch(String[] input, Repository repo) throws IOException, NoSuchAlgorithmException, DataFormatException {
        String command = input[1];
        switch (command) {

            case "init":
                InitCommand init = new InitCommand(repo);
                init.initialize();
                break;

            case "add":
                if (input.length < 3) {
                    System.out.println("Syntax: ngit add <file>");
                    return;
                }

                AddCommand add = new AddCommand(repo);
                add.add(Path.of(input[2]).toAbsolutePath());
                break;

            case "branch":
                if (input.length < 3) {
                    System.out.println("Syntax: ngit branch <branch-name>");
                    return;
                }

                BranchCommand branch = new BranchCommand(repo);
                branch.createBranch(input[2]);
                break;

            case "checkout":
                if (input.length < 3) {
                    System.out.println("Syntax: ngit checkout <branch-name>");
                    return;
                }

                CheckoutCommand checkout = new CheckoutCommand(repo);
                checkout.Checkout(input[2]);
                break;

            case "commit":
                if (input.length < 4 || !"-m".equals(input[2])) {
                    System.out.println("Syntax: ngit commit -m \"message\"");
                    return;
                }

                CommitCommand commit = new CommitCommand(repo);
                commit.Commit(input[3]);
                break;

            case "status":
                StatusCommand status = new StatusCommand(repo);
                status.status();
                break;

            case "log":
                Log log = new Log(repo);
                log.ShowLog();
                break;

            case "exit":
                System.exit(0);

            default:
                System.out.println("Command not found: " + command);
        }
    }
    public static List<String> tokenizer(String input){
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean isInQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                // change true to false and vice versa
                isInQuotes = !isInQuotes;
            }
            else if (c == ' ' && !isInQuotes) {
                // if a space is encountered, and it is not in quotes, or we have reached the end quote, we add that word to the list and set the string builder to be 0
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }
            else {
                // add every char to the current word
                current.append(c);
            }
        }
        if (current.length() > 0){
            tokens.add(current.toString());
        }
        return tokens;
    }
}