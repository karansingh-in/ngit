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
        path = path.normalize();
        Path repoRoot = repo.getRepoRoot();
        if (Files.isDirectory(path)) {
            try (Stream<Path> allFiles = Files.walk(path)) {
                List<Path> files = allFiles
                        .filter(Files::isRegularFile)
                        .filter(f -> !f.startsWith(repo.getNgit()) && (repo.getRepoRoot() == null || !f.startsWith(repo.getRepoRoot().resolve(".git"))))
                        .toList();
                for (Path file : files) {
                    add(file);
                }
            }
            return;
        }

        byte[] fileData = Files.readAllBytes(path);
        String hash = ObjectStore.store(fileData, repo);

        Index index = new Index(repo);
        index.load();

        Path relativePath = repoRoot.relativize(path);
        index.put(relativePath, hash);
        index.save();
    }
}
