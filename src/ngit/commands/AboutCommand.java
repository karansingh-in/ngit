package ngit.commands;

public class AboutCommand {
    public static void aboutThisProject(){
        System.out.println("""
                ╔══════════════════════════════════════════╗
                ║                 ngit                     ║
                ║       A tiny Git-like VCS in Java        ║
                ╚══════════════════════════════════════════╝
                
                Built by Karan Singh
                GitHub: karansingh-in
                
                ngit is a small version control system built from scratch
                to understand how Git works under the hood.
                
                Why I built it:
                I use Git all the time, but I wanted to understand what
                actually happens behind commands like add, commit, branch,
                diff, and merge.
                
                Built with:
                  • Java
                  • NIO
                  • SHA-256
                  • Plain Java — no Git libraries
                
                Currently implemented:
                  • Repository initialization
                  • File staging
                  • Content-addressed object storage
                  • Commits
                  • Branches
                  • Status
                  • LCS-based diff
                  • Three-way merge
                
                The goal isn't to recreate Git.
                The goal is to understand it.
                
                Version: 0.1
                Status: Still building.
                """);
    }
}
