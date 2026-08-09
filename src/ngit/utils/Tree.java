package ngit.utils;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

public class Tree {
    private Repository repo;

    public Tree(Repository repo){
        this.repo = repo;
    }

    public String generateTree() throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(repo.getIndex());
        return ObjectStore.store(data);
    }
}