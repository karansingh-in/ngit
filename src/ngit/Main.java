package ngit;

import ngit.commands.*;
import ngit.repository.Repository;
import ngit.utils.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, DataFormatException {

        Repository repo = new Repository();

        if (args.length == 0) {
            HelpCommand.showAllCommands("help");
            return;
        }

        dispatch(args, repo);
    }

    public static void dispatch(String[] input, Repository repo) throws IOException, NoSuchAlgorithmException, DataFormatException {
        String command = input[0];
        switch (command) {

            case "init":
                InitCommand init = new InitCommand(repo);
                init.initialize();
                break;

            case "add":
                if (input.length < 2) {
                    System.out.println("Syntax: ngit add <file>");
                    return;
                }

                AddCommand add = new AddCommand(repo);
                add.add(Path.of(input[1]).toAbsolutePath().normalize());
                break;

            case "branch":
                BranchCommand branch = new BranchCommand(repo);

                if (input.length == 1) {
                    branch.listBranches();
                } else {
                    branch.createBranch(input[1]);
                }
                break;

            case "checkout":
                if (input.length < 2) {
                    System.out.println("usage: ngit checkout <branch>");
                    return;
                }

                CheckoutCommand checkout = new CheckoutCommand(repo);
                checkout.Checkout(input[1]);
                break;

            case "commit":
                if (input.length < 3 || !"-m".equals(input[1])) {
                    System.out.println("usage: ngit commit -m <message>");
                    return;
                }

                CommitCommand commit = new CommitCommand(repo);
                commit.Commit(input[2]);
                break;

            case "status":
                StatusCommand status = new StatusCommand(repo);
                status.status();
                break;

            case "diff":
                if (input.length < 2) {
                    System.out.println("usage: ngit diff <file>");
                    return;
                }

                DiffCommand diff = new DiffCommand(repo);
                diff.diff(Path.of(input[1]).toAbsolutePath().normalize());
                break;

            case "log":
                Log log = new Log(repo);
                log.ShowLog();
                break;

            case "about":
                AboutCommand.aboutThisProject();
                break;

            case "##":
                EasterEggCommand.myPersonalTouch();
                break;

            case "help":
                if (input.length == 1) {
                    HelpCommand.showAllCommands("help");
                } else {
                    HelpCommand.showAllCommands(input[1]);
                }
                break;

            case "merge":
                if (input.length < 2) {
                    System.out.println("usage: ngit merge <branch>");
                    return;
                }

                MergeCommand merge = new MergeCommand(repo);
                merge.merge(input[1]);
                break;

            default:
                System.out.println("ngit: '" + command + "' is not a ngit command.");
                System.out.println("See 'ngit help' for usage.");
        }
    }
}