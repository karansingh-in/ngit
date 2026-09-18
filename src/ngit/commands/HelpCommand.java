package ngit.commands;

public class HelpCommand {

    public static void showAllCommands(String command) {

        switch (command) {
            case "init" -> System.out.println("""
                    init
                        Initialize a new ngit repository
                        Usage: ngit init
                    """);

            case "add" -> System.out.println("""
                    add <file>
                        Stage a file
                        Usage: ngit add <file>
                    """);

            case "status" -> System.out.println("""
                    status
                        Show staged and modified files
                        Usage: ngit status
                    """);

            case "commit" -> System.out.println("""
                    commit <message>
                        Create a commit
                        Usage: ngit commit "<message>"
                    """);

            case "branch" -> System.out.println("""
                    branch
                        List branches
                        Usage: ngit branch
                    
                    branch <name>
                        Create a new branch
                        Usage: ngit branch <name>
                    """);

            case "checkout" -> System.out.println("""
                    checkout <branch>
                        Switch to a branch
                        Usage: ngit checkout <branch>
                    """);

            case "diff" -> System.out.println("""
                    diff
                        Show differences between working files and the index
                        Usage: ngit diff
                    """);

            case "merge" -> System.out.println("""
                    merge <branch>
                        Merge another branch into the current branch
                        Usage: ngit merge <branch>
                    """);

            case "about" -> System.out.println("""
                    about
                        Show information about ngit
                        Usage: ngit about
                    """);

            case "help" -> System.out.println("""
                    help
                        Show this help message
                        Usage: ngit help
                    """);

            default -> System.out.println("Unknown command: " + command);
        }
    }
}