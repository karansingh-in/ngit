package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Index;
import ngit.utils.ObjectStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class AddCommand {
    private Repository repo;
    public AddCommand(Repository repo){
        this.repo = repo;
    }
    public void add(Path path) throws IOException, NoSuchAlgorithmException {
        //the path should be absolute
        Path repoRoot = repo.getRepoRoot();
        path = repoRoot.relativize(path);
        byte[] data = Files.readAllBytes(path);
        String hash = ObjectStore.store(data);
        Index index = new Index(repo);
        index.load();
        index.put(path, hash);
        index.save();
    }
}
