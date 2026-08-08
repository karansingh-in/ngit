package ngit.repository;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

public class Repository {

    Path ngit;
    Path objects;
    Path refs;
    Path index;
    Path HEAD;
    void initialize() throws IOException{
        String [] init_folder_structure = {
                ".ngit",
                ".ngit/refs",
                ".ngit/refs/heads",
                ".ngit/objects"
        };

        for(String folder: init_folder_structure){
            Files.createDirectories(Path.of(folder));
        }

        Path p1 = Path.of(".ngit/index.txt");
        Path p2 = Path.of(".ngit/HEAD.txt");

        Files.createFile(p1);
        Files.createFile(p2);

    }

    void findRepository(){
        Path repoRoot = Path.of(".");
        repoRoot = repoRoot.toAbsolutePath();
        while(!Files.isDirectory(repoRoot.resolve(".ngit"))){
            repoRoot = repoRoot.getParent();
            if (repoRoot == null) {
                return;
            }
        }
        this.ngit = repoRoot.resolve(".ngit");
        this.objects = ngit.resolve("objects");
        this.refs = ngit.resolve("refs");
        this.index = ngit.resolve("index.txt");
        this.HEAD = ngit.resolve("HEAD.txt");
    }
}
