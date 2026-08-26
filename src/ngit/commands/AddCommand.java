package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Index;
import ngit.utils.ObjectStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Stream;

public class AddCommand {
    private Repository repo;
    public AddCommand(Repository repo){
        this.repo = repo;
    }
    public void add(Path path) throws IOException, NoSuchAlgorithmException {

        Path repoRoot = repo.getRepoRoot();
        if (Files.isDirectory(path)) {
            Stream<Path> allFiles = Files.walk(path);
            List<Path> files = allFiles.toList();
            for (Path file : files) {
                if (Files.isRegularFile(file)) {
                    add(file);
                }
            }
            return;
        }

        byte[] fileData = Files.readAllBytes(path);
        String hash = ObjectStore.store(fileData);

        Index index = new Index(repo);
        index.load();

        Path relativePath = repoRoot.relativize(path);
        index.put(relativePath, hash);
        index.save();
    }
}
