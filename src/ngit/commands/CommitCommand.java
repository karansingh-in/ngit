package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Commit;
import ngit.utils.Tree;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CommitCommand {
    private Repository repo;

    public CommitCommand(Repository repo){
        this.repo = repo;
    }

    public void Commit(String commitMessage) throws IOException, NoSuchAlgorithmException {
        Tree tree = new Tree(repo);
        String treeHash = tree.generateTree();

        Commit newCommit = new Commit(repo);
        newCommit.createCommit(commitMessage, treeHash);
    }
}
