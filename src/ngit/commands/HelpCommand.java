package ngit.commands;

public class HelpCommand {

    public static void showAllCommands(String command) {

        if (command == null) {
            System.out.println("""
                
                usage: ngit <command> [arguments]
                
                Commands:
                  init                  Initialize a new repository
                  add <file>            Stage a file
                  status                Show the working tree status
                  commit -m <message>   Record changes
                  branch [name]         List or create branches
                  checkout <branch>     Switch branches
                  log                   Show commit history
                  diff <file>           Show changes
                  merge <branch>        Merge a branch
                  help [command]        Show help for a command
                  about                 About ngit
                
                """);
            return;
        }

        switch (command) {

            case "init":
                System.out.println("""
                    
                    ngit init
                    
                    Initialize a new ngit repository.
                    """);
                break;

            case "add":
                System.out.println("""
                    
                    ngit add <file>
                    
                    Stage a file for the next commit.
                    """);
                break;

            case "status":
                System.out.println("""
                    
                    ngit status
                    
                    Show the current state of the working tree.
                    """);
                break;

            case "commit":
                System.out.println("""
                    
                    ngit commit -m <message>
                    
                    Record the staged changes.
                    """);
                break;

            case "branch":
                System.out.println("""
                    
                    ngit branch [name]
                    
                    List existing branches or create a new branch.
                    """);
                break;

            case "checkout":
                System.out.println("""
                    
                    ngit checkout <branch>
                    
                    Switch to another branch.
                    """);
                break;

            case "log":
                System.out.println("""
                    
                    ngit log
                    
                    Show the commit history.
                    """);
                break;

            case "diff":
                System.out.println("""
                    
                    ngit diff <file>
                    
                    Show changes made to a file.
                    """);
                break;

            case "merge":
                System.out.println("""
                    
                    ngit merge <branch>
                    
                    Merge another branch into the current branch.
                    """);
                break;

            case "about":
                System.out.println("""
                    
                    ngit about
                    
                    Show information about ngit.
                    """);
                break;

            case "help":
                showAllCommands(null);
                break;

            default:
                System.out.println("ngit: no help topics match '" + command + "'.");
                System.out.println("See 'ngit help' for available commands.");
        }
    }
}