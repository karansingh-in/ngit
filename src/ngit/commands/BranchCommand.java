package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Branch;

import java.io.IOException;

public class BranchCommand {
    private Repository repo;

    public BranchCommand(Repository repo){
        this.repo = repo;
    }

    public void createBranch(String branchName) throws IOException {
        Branch branch = new Branch(repo);
        branch.createBranch(branchName);
    }
}
