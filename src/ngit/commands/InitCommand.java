package ngit.commands;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.file.Files;

public class InitCommand {
    private Repository repo;
    public InitCommand(Repository repo){
        this.repo = repo;
    }
    public void initialize() throws IOException {
        repo.initialize();
        Files.writeString(repo.getHEAD(), "heads/main");
    }
}
