package ngit.utils;

import ngit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;

public class Log {
    private Repository repo;
    public Log(Repository repo){
        this.repo = repo;
    }

    public void ShowLog() throws IOException, DataFormatException {
        String head = Files.readString(repo.getHEAD()).trim();
        Path currentBranch = repo.getRefs().resolve(head);
        if (!Files.exists(currentBranch)) {
            System.out.println("No commits yet.");
            return;
        }
        String currentHash = Files.readString(currentBranch).trim();

        if(currentHash.isBlank()){
            System.out.println("No commits yet.");
            return;
        }
        while(!currentHash.isBlank()){
            String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
            System.out.println("commit " + currentHash);
            System.out.println(commitMetadata);
            System.out.println("----------------------------------------");
            
            String[] lines = commitMetadata.split("\r?\n");
            String parentHash = "";
            for (String line : lines) {
                if (line.startsWith("Parent:")) {
                    parentHash = line.substring(7).trim();
                    break;
                }
            }
            currentHash = parentHash;
        }
    }
}