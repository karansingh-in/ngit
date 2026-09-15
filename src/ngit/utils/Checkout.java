package ngit.utils;

import ngit.commands.StatusCommand;
import ngit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import static ngit.utils.CompressUtil.decompress;

public class Checkout {
    private Repository repo;

    public Checkout(Repository repo){
        this.repo = repo;
    }

//    public void switchBranch(String branchName) throws IOException, DataFormatException, NoSuchAlgorithmException {
//        Path branch = repo.getRefs().resolve("heads").resolve(branchName);
//        //check if the branch exists
//        if(!Files.exists(branch)){
//            System.out.println("The branch "+ branchName + " does not exist!");
//            return;
//        }
//
//        String currentHead = Files.readString(repo.getHEAD()).trim();
//        if (currentHead.equals("heads/" + branchName)) {
//            System.out.println("Already on branch '" + branchName + "'");
//            return;
//        }
//
//        //checking if all the files are committed before changing the branch because uncommitted files will be lost
//        StatusCommand st = new StatusCommand(repo);
//        st.status();
//        if(st.isClean()) {
//            cleanFiles(st.getTrackedFiles());
//            loadBranch(branchName);
//            updateHead(branchName);
//            System.out.println("Switched to branch '" + branchName + "'");
//        } else {
//            System.out.println("Cannot switch branch: you have uncommitted or untracked changes. Please commit them first.");
//        }
//    }

    public void switchBranch(String branchName) throws IOException, DataFormatException, NoSuchAlgorithmException {
        Path branch = repo.getRefs().resolve("heads").resolve(branchName);
        if (!Files.exists(branch)) {
            System.out.println("The branch " + branchName + " does not exist!");
            return;
        }

        String currentHead = Files.readString(repo.getHEAD()).trim();
        if (currentHead.equals("heads/" + branchName)) {
            System.out.println("Already on branch '" + branchName + "'");
            return;
        }

        StatusCommand st = new StatusCommand(repo);
        st.status();
        if (!st.isClean()) {
            System.out.println("Cannot switch branch: you have uncommitted or untracked changes. Please commit them first.");
            return;
        }

        // resolve FIRST. nothing destructive has happened yet.
        Map<String, String> targetTree = resolveTree(branchName); // returns {} only when branch truly has no commits

        // now safe to mutate
        cleanFiles(st.getTrackedFiles());
        applyTree(targetTree);
        updateHead(branchName);
        System.out.println("Switched to branch '" + branchName + "'");
    }

    private Map<String, String> resolveTree(String branchName) throws IOException, DataFormatException {
        Path branchRef = repo.getRefs().resolve("heads").resolve(branchName);
        String currentHash = Files.readString(branchRef).trim();
        Map<String, String> tree = new LinkedHashMap<>();
        if (currentHash.isBlank()) return tree; // legit empty branch, not a failure

        String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
        String treeHash = null;
        for (String line : commitMetadata.split("\r?\n")) {
            if (line.startsWith("Tree:")) { treeHash = line.substring(5).trim(); break; }
        }
        if (treeHash == null || treeHash.isBlank()) return tree;

        String index = CompressUtil.decompressToString(repo.getObjects().resolve(treeHash));
        for (String line : index.split("\r?\n")) {
            line = line.trim();
            if (line.isBlank()) continue;
            int commaIdx = line.indexOf(",");
            if (commaIdx == -1) continue;
            tree.put(line.substring(0, commaIdx).trim(), line.substring(commaIdx + 1).trim());
        }
        return tree;
    }

    private void applyTree(Map<String, String> tree) throws IOException, DataFormatException {
        StringBuilder indexContent = new StringBuilder();
        for (var e : tree.entrySet()) {
            indexContent.append(e.getKey()).append(",").append(e.getValue()).append("\n");
            Path path = repo.getRepoRoot().resolve(e.getKey());
            byte[] compressed = Files.readAllBytes(repo.getObjects().resolve(e.getValue()));
            createFiles(path, decompress(compressed));
        }
        Files.writeString(repo.getIndex(), indexContent.toString());
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
        System.out.println("branch name:" + branchName);
        Path branchRef = repo.getRefs().resolve("heads").resolve(branchName);
        // currentHash is the last commit, i.e., the last saved state of the branch
        String currentHash = Files.readString(branchRef).trim();
        if (currentHash.isBlank()){
            Files.writeString(repo.getIndex(), "");
            return;
        }
        System.out.println("branch ref:" + branchRef);
        System.out.println("current hash:" + currentHash );
        /* each commit contains metadata:
           treehash
           parent(the previous commit)
           commit message
           timestamp of commit
         */
        String commitMetadata = CompressUtil.decompressToString(repo.getObjects().resolve(currentHash));
        String[] lines = commitMetadata.split("\r?\n"); // CRLF
        String treeHash = null;
        for (String line : lines) {
            if (line.startsWith("Tree:")) {
                treeHash = line.substring(5).trim();
                break;
            }
        }
        System.out.println(commitMetadata);
        System.out.println(treeHash);
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
            int commaIdx = line.indexOf(","); //need to fix: if a filename contains "," in it, the sys breaks.
            if (commaIdx == -1) continue;

            String relativeFilePath = line.substring(0, commaIdx).trim();
            String blobHash = line.substring(commaIdx + 1).trim();

            Path path = repo.getRepoRoot().resolve(relativeFilePath);
            byte[] compressedData = Files.readAllBytes(repo.getObjects().resolve(blobHash));
            byte[] decompressedData = decompress(compressedData);
            createFiles(path, decompressedData);
        }
    }

    public void updateHead(String branchName) throws IOException {
        //change the current branch's name in the HEAD file to the new branch's name
        Path branchPointer = repo.getHEAD();
        String newBranch = "heads/" + branchName;
        Files.writeString(branchPointer, newBranch);
    }
}