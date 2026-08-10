package ngit.utils;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Commit {
    private Repository repo;
    public Commit(Repository repo){
        this.repo = repo;
    }

    public void createCommit(String commitMessage, String treeHash) throws IOException, NoSuchAlgorithmException {

        List<String> metadata = new ArrayList<>();
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy,hh:mm:ss");
        String formattedDateTime = datetime.format(datetimeFormat);

        String currentBranch = repo.getRefs() + Files.readString(repo.getHEAD());
        Path path = Path.of(currentBranch);
        String parentCommit = Files.readString(path);
        if (parentCommit.isBlank()){
            parentCommit = null;
        }
        metadata.add("Tree:" + treeHash);
        metadata.add("Parent:" + parentCommit);
        metadata.add("CommitMessage:" + commitMessage);
        metadata.add("TimeStamp:" + formattedDateTime);

        String allData = String.join("\n", metadata);
        byte[] data = allData.getBytes(StandardCharsets.UTF_8);
        String currentHash = ObjectStore.store(data);
        Files.writeString(path, currentHash);
    }
}
