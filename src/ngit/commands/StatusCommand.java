package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.HashUtil;
import ngit.utils.Index;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StatusCommand {
    private Repository repo;
    private List<String> modified = new ArrayList<>();
    private List<String> untracked = new ArrayList<>();
    private List<String> ontrack = new ArrayList<>();
    public StatusCommand(Repository repo){
        this.repo = repo;
    }
    public void status() throws IOException, NoSuchAlgorithmException {
        modified.clear();
        untracked.clear();
        ontrack.clear();
        Path repoRoot = repo.getRepoRoot();
        Index index = new Index(repo);
        index.load();

        try (Stream<Path> all_files = Files.walk(repoRoot)) {
            List<Path> files = all_files
                    .filter(Files::isRegularFile)
                    .filter(n -> !n.startsWith(repo.getNgit()) && (repo.getRepoRoot() == null || !n.startsWith(repo.getRepoRoot().resolve(".git"))))
                    .toList();

            for (Path path: files){
                Path relativePath = repoRoot.relativize(path);
                // index contains relative path
                if (index.containsFile(relativePath)){
                    byte[] data = Files.readAllBytes(path);
                    String currentHash = HashUtil.generateHash(data);
                    //again we are accessing index so relative path
                    if (index.containsHash(relativePath, currentHash)){
                        ontrack.add(String.valueOf(relativePath));
                    }
                    else{
                        modified.add(String.valueOf(relativePath));
                    }
                }
                else{
                    untracked.add(String.valueOf(relativePath));
                }
            }
        }
        printStatus();
    }

    public void printStatus(){
        System.out.println("\u001B[31mModified:"); //Red color
        for (String file : modified){
            System.out.println(file);
        }
        System.out.println("\u001B[0m ");
        System.out.println("\u001B[32mOn Track:"); //Green color
        for (String file : ontrack){
            System.out.println(file);
        }
        System.out.println("\u001B[0m ");
        System.out.println("\u001B[33mUntracked:"); //Yellow color
        for (String file : untracked){
            System.out.println(file);
        }
        System.out.println("\u001B[0m ");
    }

    public boolean isClean(){
        return modified.isEmpty() && untracked.isEmpty();
    }

    public List<String> getTrackedFiles(){
        return ontrack;
    }
}