package ngit.utils;

import ngit.commands.StatusCommand;
import ngit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.DataFormatException;

import static ngit.utils.CompressUtil.decompress;

public class Checkout {
    private Repository repo;

    public Checkout(Repository repo){
        this.repo = repo;
    }

    public void switchBranch(String branchName) throws IOException, DataFormatException, NoSuchAlgorithmException {
        Path branch = repo.getRefs().resolve("heads").resolve(branchName);
        //check if the branch exists
        if(!Files.exists(branch)){
            System.out.println("The branch "+ branchName + " does not exist!");
            return;
        }
        //checking if all the files are commited before changing the branch because the files not commited will likely be lost
        StatusCommand st = new StatusCommand(repo);
        st.status();
        if(st.isClean()) {
            cleanFiles(st.getTrackedFiles());
            loadBranch(branchName);
            finalize(branchName);
        }
    }

    public void cleanFiles(List<String> files) throws IOException {
        for (String file : files){
            Files.delete(repo.getRepoRoot().resolve(file));
        }
    }

    public void createFiles(Path path, byte[] data) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        //writing files fails if parent directory doesn't exist
        Files.write(path,data);
    }

    public void loadBranch(String branchName) throws DataFormatException, IOException {

        String currentHash = Files.readString(repo.getRefs().resolve("heads").resolve(branchName));
        if(currentHash.isBlank()){return;}

        String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
        //first line of the commit metadata is tree hash
        String firstLine = commitMetadata.substring(0, commitMetadata.indexOf("\n"));
        //extract the hash from treeHash:ckbafkbveaifae
        String treeHash = firstLine.substring(firstLine.indexOf(":") + 1).trim();
        //tree contains compressed index
        String index = CompressUtil.decompressToString(repo.getObjects().resolve(treeHash));
        String[] contents = index.split("\n");

        Path path = null;
        String blobHash = null;
        for (String line : contents){
            path = repo.getRepoRoot().resolve(line.substring(0, line.indexOf(",")));
            blobHash = line.substring(line.indexOf(",")+1);

            byte[] compressedData = Files.readAllBytes(repo.getObjects().resolve(blobHash));
            byte[] decompressedData = decompress(compressedData);
            createFiles(path, decompressedData);
        }
    }

    public void finalize(String branchName) throws IOException {
        //change the current branch text in HEAD to the new branch
        Path branchPointer = repo.getHEAD();
        String newBranch = "heads/"+branchName;
        Files.writeString(branchPointer, newBranch);
    }
}