package ngit.utils;

import ngit.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Diff {

    private Repository repo;

    Diff(Repository repo) {
        this.repo = repo;
    }

    private static class LCSResult {
        List<String> lcs;
        int[][] dp;

        LCSResult(List<String> lcs, int[][] dp) {
            this.lcs = lcs;
            this.dp = dp;
        }
    }

    private static LCSResult generateLCS(List<String> oldFile, List<String> newFile) {

        int oldSize = oldFile.size();
        int newSize = newFile.size();

        int[][] dp = new int[oldSize + 1][newSize + 1];

        // Construct LCS table
        for (int i = 1; i <= oldSize; i++) {
            for (int j = 1; j <= newSize; j++) {

                if (oldFile.get(i - 1).equals(newFile.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(
                            dp[i - 1][j],
                            dp[i][j - 1]
                    );
                }
            }
        }

        // Reconstruct LCS
        LinkedList<String> lcs = new LinkedList<>();

        int i = oldSize;
        int j = newSize;

        while (i > 0 && j > 0) {

            if (oldFile.get(i - 1).equals(newFile.get(j - 1))) {
                lcs.addFirst(oldFile.get(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return new LCSResult(lcs, dp);
    }

    public static List<String> checkDiff(
            List<String> oldFile,
            List<String> newFile
    ) {

        LCSResult lcsResult = generateLCS(oldFile, newFile);

        List<String> lcs = lcsResult.lcs;
        int[][] dp = lcsResult.dp;

        int j = oldFile.size();
        int i = newFile.size();
        int k = lcs.size();

        LinkedList<String> result = new LinkedList<>();

        while (i > 0 && j > 0 && k > 0) {

            String oldLine = oldFile.get(j - 1);
            String newLine = newFile.get(i - 1);
            String lcsLine = lcs.get(k - 1);

            // Both lines are part of the LCS
            if (oldLine.equals(lcsLine) && newLine.equals(lcsLine)) {

                result.addFirst("  " + oldLine);

                i--;
                j--;
                k--;
            }

            // Old line is not part of LCS, new line is
            else if (!oldLine.equals(lcsLine) && newLine.equals(lcsLine)) {

                result.addFirst("- " + oldLine);

                j--;
            }

            // Old line is part of LCS, new line is not
            else if (oldLine.equals(lcsLine) && !newLine.equals(lcsLine)) {

                result.addFirst("+ " + newLine);

                i--;
            }

            // Neither line is part of the LCS
            else {

                if (dp[j - 1][i] >= dp[j][i - 1]) {

                    result.addFirst("- " + oldLine);
                    j--;

                } else {

                    result.addFirst("+ " + newLine);
                    i--;
                }
            }
        }

        // Remaining old lines = deletions
        while (j > 0) {
            result.addFirst("- " + oldFile.get(j - 1));
            j--;
        }

        // Remaining new lines = additions
        while (i > 0) {
            result.addFirst("+ " + newFile.get(i - 1));
            i--;
        }

        return result;
    }
}