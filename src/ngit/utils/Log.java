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
        Path currentBranch = repo.getRefs().resolve(Files.readString(repo.getHEAD()));
        String currentHash = Files.readString(currentBranch);
        currentHash = currentHash.trim();

        if(currentHash.isBlank()){
            System.out.println("No commits yet.");
            return;
        }
        while(!currentHash.isBlank()){
        String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
        System.out.println(commitMetadata);
        //second line of the commit metadata is parent hash
        String secondLine = commitMetadata.split("\n")[1];
        //extract the hash from parentHash:ckbafkbveaifae
        String parentHash = secondLine.substring(secondLine.indexOf(":") + 1).trim();

        currentHash = parentHash;
        }
    }
}