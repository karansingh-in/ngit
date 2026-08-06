package ngit.repository;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

public class Repository {
    public static void main(String[] args) throws IOException{
        String [] init_folder_structure = {
                ".ngit",
                ".ngit/refs",
                ".ngit/refs/heads",
                ".ngit/objects"
        };

        for(String folder: init_folder_structure){
            Files.createDirectories(Path.of(folder));
        }

        Path p1 = Path.of("index.txt");
        Path p2 = Path.of("HEAD.txt");

        Files.createFile(p1);
        Files.createFile(p2);

    }
}
