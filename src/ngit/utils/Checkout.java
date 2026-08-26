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

        String currentHead = Files.readString(repo.getHEAD()).trim();
        if (currentHead.equals("heads/" + branchName)) {
            System.out.println("Already on branch '" + branchName + "'");
            return;
        }

        //checking if all the files are committed before changing the branch because uncommitted files will be lost
        StatusCommand st = new StatusCommand(repo);
        st.status();
        if(st.isClean()) {
            cleanFiles(st.getTrackedFiles());
            loadBranch(branchName);
            finalize(branchName);
            System.out.println("Switched to branch '" + branchName + "'");
        } else {
            System.out.println("Cannot switch branch: you have uncommitted or untracked changes. Please commit them first.");
        }
    }

    public void cleanFiles(List<String> files) throws IOException {
        for (String file : files) {
            Path path = repo.getRepoRoot().resolve(file);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                System.out.println("Deleting: " + file);
                Files.delete(path);
            }
        }
    }

    public void createFiles(Path path, byte[] data) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        //writing files fails if parent directory doesn't exist
        Files.write(path, data);
    }

    public void loadBranch(String branchName) throws DataFormatException, IOException {
        Path branchRef = repo.getRefs().resolve("heads").resolve(branchName);
        String currentHash = Files.readString(branchRef).trim();
        if (currentHash.isBlank()){
            Files.writeString(repo.getIndex(), "");
            return;
        }

        String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
        String[] lines = commitMetadata.split("\r?\n");
        String treeHash = null;
        for (String line : lines) {
            if (line.startsWith("Tree:")) {
                treeHash = line.substring(5).trim();
                break;
            }
        }

        if (treeHash == null || treeHash.isBlank()) {
            Files.writeString(repo.getIndex(), "");
            return;
        }

        //tree contains compressed index
        String index = CompressUtil.decompressToString(repo.getObjects().resolve(treeHash));
        Files.writeString(repo.getIndex(), index);
        String[] contents = index.split("\r?\n");

        for (String line : contents){
            line = line.trim();
            if (line.isBlank()) continue;
            int commaIdx = line.indexOf(",");
            if (commaIdx == -1) continue;

            String relativeFilePath = line.substring(0, commaIdx).trim();
            String blobHash = line.substring(commaIdx + 1).trim();

            Path path = repo.getRepoRoot().resolve(relativeFilePath);
            byte[] compressedData = Files.readAllBytes(repo.getObjects().resolve(blobHash));
            byte[] decompressedData = decompress(compressedData);
            createFiles(path, decompressedData);
        }
    }

    public void finalize(String branchName) throws IOException {
        //change the current branch text in HEAD to the new branch
        Path branchPointer = repo.getHEAD();
        String newBranch = "heads/" + branchName;
        Files.writeString(branchPointer, newBranch);
    }
}