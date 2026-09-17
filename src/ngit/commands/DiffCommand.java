package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.CompressUtil;
import ngit.utils.Diff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.DataFormatException;

public class DiffCommand {
    private final Repository repo;
    public DiffCommand(Repository repo) {
        this.repo = repo;
    }

    public void diff(Path file) throws IOException, DataFormatException {
        // Get the file path relative to repository root
        String fileName = repo.getRepoRoot()
                .relativize(file)
                .toString();
        // Find the file's hash in the index
        List<String> index = Files.readAllLines(repo.getIndex());
        String hash = null;

        for (String line : index) {
            String[] parts = line.split(",");
            if (parts.length == 2 && parts[0].equals(fileName)) {
                hash = parts[1];
                break;
            }
        }

        if (hash == null) {
            System.out.println("File not found in index.");
            return;
        }

        // Read committed version
        Path object = repo.getObjects().resolve(hash);

        byte[] data = CompressUtil.decompress(
                Files.readAllBytes(object)
        );

        List<String> oldLines = List.of(
                new String(data, StandardCharsets.UTF_8).split("\n", -1)
        );

        // Read working-tree version
        List<String> newLines = Files.readAllLines(file);

        // Run LCS diff
        List<String> result = Diff.checkDiff(oldLines, newLines);

        for (String line : result) {
            System.out.println(line);
        }
    }
}