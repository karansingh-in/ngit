package ngit.utils;

import ngit.repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.DataFormatException;

public class Merge {

    private final Repository repo;
    private boolean hasConflict = false;

    public Merge(Repository repo) {
        this.repo = repo;
    }

    public void merge(String branch) throws IOException, DataFormatException {
        String currentBranch = Files.readString(repo.getHEAD()).trim().substring(6);

        String ours = Files.readString(repo.getRefs().resolve("heads").resolve(currentBranch)).trim();
        String theirs = Files.readString(repo.getRefs().resolve("heads").resolve(branch)).trim();
        String base = findBase(ours, theirs);

        Map<String, String> baseTree = getTree(base);
        Map<String, String> ourTree = getTree(ours);
        Map<String, String> theirTree = getTree(theirs);

        Set<String> files = new HashSet<>(baseTree.keySet());
        files.addAll(ourTree.keySet());
        files.addAll(theirTree.keySet());

        for (String file : files) {
            mergeFile(file, baseTree.get(file), ourTree.get(file), theirTree.get(file));
        }

        System.out.println(hasConflict
                ? "Merge completed with conflicts. Resolve markers before committing."
                : "Merge completed.");
    }

    private void mergeFile(String file, String baseHash, String ourHash, String theirHash) throws IOException, DataFormatException {
        // Same on both branches
        if (Objects.equals(ourHash, theirHash)) {
            write(file, ourHash);
            return;
        }
        // Only theirs changed it (includes deletion when theirHash == null)
        if (Objects.equals(baseHash, ourHash)) {
            write(file, theirHash);
            return;
        }
        // Only ours changed it (includes deletion when ourHash == null)
        if (Objects.equals(baseHash, theirHash)) {
            write(file, ourHash);
            return;
        }

        // Both changed it -> real 3-way merge
        List<String> base = lines(baseHash);
        List<Change> ourChanges = diff(base, lines(ourHash));
        List<Change> theirChanges = diff(base, lines(theirHash));

        List<String> result = threeWayMerge(base, ourChanges, theirChanges, file);
        Files.write(repo.getRepoRoot().resolve(file), result);
    }

    private List<String> threeWayMerge(List<String> base, List<Change> ourChanges, List<Change> theirChanges, String file) {
        List<String> result = new ArrayList<>();
        int position = 0, oi = 0, ti = 0;

        while (oi < ourChanges.size() || ti < theirChanges.size()) {
            Change ourChange = oi < ourChanges.size() ? ourChanges.get(oi) : null;
            Change theirChange = ti < theirChanges.size() ? theirChanges.get(ti) : null;

            boolean overlap = ourChange != null && theirChange != null
                    && ourChange.start() < theirChange.end() && theirChange.start() < ourChange.end();

            if (overlap) {
                int regionStart = Math.min(ourChange.start(), theirChange.start());
                result.addAll(base.subList(position, regionStart));

                int regionEnd = Math.max(ourChange.end(), theirChange.end());
                List<String> ourLines = new ArrayList<>();
                List<String> theirLines = new ArrayList<>();

                // pull in any chained/adjacent changes that fall inside the growing region
                while (oi < ourChanges.size() && ourChanges.get(oi).start() < regionEnd) {
                    ourLines.addAll(ourChanges.get(oi).lines());
                    regionEnd = Math.max(regionEnd, ourChanges.get(oi).end());
                    oi++;
                }
                while (ti < theirChanges.size() && theirChanges.get(ti).start() < regionEnd) {
                    theirLines.addAll(theirChanges.get(ti).lines());
                    regionEnd = Math.max(regionEnd, theirChanges.get(ti).end());
                    ti++;
                }

                if (ourLines.equals(theirLines)) {
                    result.addAll(ourLines);
                } else {
                    result.add("<<<<<<< HEAD");
                    result.addAll(ourLines);
                    result.add("=======");
                    result.addAll(theirLines);
                    result.add(">>>>>>> " + file);
                    hasConflict = true;
                }
                position = regionEnd;

            } else {
                boolean oursFirst = theirChange == null
                        || (ourChange != null && ourChange.start() < theirChange.start());

                Change next = oursFirst ? ourChange : theirChange;
                result.addAll(base.subList(position, next.start()));
                result.addAll(next.lines());
                position = next.end();
                if (oursFirst) oi++; else ti++;
            }
        }

        result.addAll(base.subList(position, base.size()));
        return result;
    }

    // LCS-based diff: returns changed regions as (old range -> replacement lines)
    private List<Change> diff(List<String> oldLines, List<String> newLines) {
        int n = oldLines.size(), m = newLines.size();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                dp[i][j] = oldLines.get(i - 1).equals(newLines.get(j - 1))
                        ? dp[i - 1][j - 1] + 1
                        : Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        LinkedList<Change> changes = new LinkedList<>();
        int i = n, j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && oldLines.get(i - 1).equals(newLines.get(j - 1))) {
                i--;
                j--;
                continue;
            }

            int end = i;
            List<String> added = new ArrayList<>();

            while (i > 0 && j > 0 && !oldLines.get(i - 1).equals(newLines.get(j - 1))) {
                if (dp[i - 1][j] >= dp[i][j - 1]) i--;
                else added.add(0, newLines.get(--j));
            }
            while (j > 0 && (i == 0 || dp[i][j - 1] >= dp[i - 1][j])) {
                added.add(0, newLines.get(--j));
            }

            changes.addFirst(new Change(i, end, added));
        }

        return changes;
    }

    private String findBase(String ours, String theirs) throws IOException, DataFormatException {
        Set<String> ancestors = new HashSet<>();

        while (ours != null) {
            ancestors.add(ours);
            ours = parent(ours);
        }
        while (theirs != null) {
            if (ancestors.contains(theirs)) return theirs;
            theirs = parent(theirs);
        }

        return null;
    }

    private String parent(String commit) throws IOException, DataFormatException {
        for (String line : read(commit).split("\n")) {
            if (line.startsWith("Parent:")) return line.substring(7).trim();
        }
        return null;
    }

    private Map<String, String> getTree(String commit) throws IOException, DataFormatException {
        Map<String, String> tree = new HashMap<>();
        if (commit == null) return tree;

        for (String line : read(commit).split("\n")) {
            if (!line.startsWith("Tree:")) continue;

            for (String entry : read(line.substring(5).trim()).split("\n")) {
                if (entry.isBlank()) continue;
                String[] parts = entry.split(",", 2);
                tree.put(parts[0], parts[1]);
            }
            break;
        }

        return tree;
    }

    private List<String> lines(String hash) throws IOException, DataFormatException {
        return hash == null ? new ArrayList<>() : new ArrayList<>(List.of(read(hash).split("\n", -1)));
    }

    private String read(String hash) throws IOException, DataFormatException {
        byte[] data = Files.readAllBytes(repo.getObjects().resolve(hash));
        return new String(CompressUtil.decompress(data), StandardCharsets.UTF_8);
    }

    private void write(String file, String hash) throws IOException, DataFormatException {
        var path = repo.getRepoRoot().resolve(file);
        if (hash == null) {
            Files.deleteIfExists(path);
            return;
        }
        byte[] data = CompressUtil.decompress(Files.readAllBytes(repo.getObjects().resolve(hash)));
        Files.write(path, data);
    }

    private record Change(int start, int end, List<String> lines) {}
}