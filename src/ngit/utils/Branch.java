package ngit.utils;

import ngit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Branch {
    private Repository repo;

    public Branch(Repository repo){
        this.repo = repo;
    }

    public void createBranch(String branchName) throws IOException {
        Path newBranch = repo.getRefs().resolve("heads").resolve(branchName);
        if(Files.exists(newBranch)){
            System.out.println("The branch already exists!");
            return;
        }
        // HEAD contains the current active branch as heads/branch_name
        String headContent = Files.readString(repo.getHEAD()).trim();
        Path currentBranch = repo.getRefs().resolve(headContent);
        String lastCommit = "";

        /* we take the last commit, i.e, how the working directory looked like at the time of creation of the new branch
           and make that the first commit of the new branch */
        if (Files.exists(currentBranch)) {
            lastCommit = Files.readString(currentBranch).trim();
        }
        Files.writeString(newBranch, lastCommit);
        System.out.println("Created branch '" + branchName + "'");
    }
    public void listBranches() throws IOException {
        Path heads = repo.getRefs().resolve("heads");

        if (!Files.exists(heads)) {
            return;
        }

        String currentBranch = Files.readString(repo.getHEAD()).trim()
                .replace("heads/", "");

        try (var branches = Files.list(heads)) {
            branches.forEach(path -> {
                String branchName = path.getFileName().toString();

                if (branchName.equals(currentBranch)) {
                    System.out.println("* " + branchName);
                } else {
                    System.out.println("  " + branchName);
                }
            });
        }
    }
}