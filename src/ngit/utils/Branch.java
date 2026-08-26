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
        String headContent = Files.readString(repo.getHEAD()).trim();
        Path currentBranch = repo.getRefs().resolve(headContent);
        String lastCommit = "";
        if (Files.exists(currentBranch)) {
            lastCommit = Files.readString(currentBranch).trim();
        }
        Files.writeString(newBranch, lastCommit);
        System.out.println("Created branch '" + branchName + "'");
    }
}