package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Merge;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

public class MergeCommand {

    private final Repository repo;

    public MergeCommand(Repository repo) {
        this.repo = repo;
    }

    public void merge(String branchName)
            throws IOException, NoSuchAlgorithmException, DataFormatException {

        Merge merge = new Merge(repo);
        merge.merge(branchName);
    }
}