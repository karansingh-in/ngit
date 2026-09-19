package ngit.repository;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

public class Repository {

    private Path ngit;
    private Path objects;
    private Path refs;
    private Path index;
    private Path HEAD;
    private Path repoRoot;
    private Path rootSearchPath;

    public Repository() {
        this(Path.of("."));
    }

    public Repository(Path startPath) {
        this.rootSearchPath = startPath.toAbsolutePath().normalize();
        findRepository(this.rootSearchPath);
    }

    public Path getNgit(){
        return ngit;
    }

    public Path getRepoRoot() {
        return repoRoot;
    }

    public Path getObjects() {
        return objects;
    }

    public Path getIndex() {
        return index;
    }

    public Path getRefs() {
        return refs;
    }

    public Path getHEAD() {
        return HEAD;
    }

    public void initialize() throws IOException {
        Path targetDir = (this.rootSearchPath != null) ? this.rootSearchPath : Path.of(".").toAbsolutePath().normalize();
        initialize(targetDir);
    }

    public void initialize(Path targetDir) throws IOException {
        Path ngitDir = targetDir.toAbsolutePath().normalize().resolve(".ngit");

        Files.createDirectories(ngitDir.resolve("refs/heads"));
        Files.createDirectories(ngitDir.resolve("objects"));

        Path p1 = ngitDir.resolve("index.txt");
        Path p2 = ngitDir.resolve("HEAD.txt");
        Path p3 = ngitDir.resolve("refs/heads/main");

        if (!Files.exists(p1)) Files.createFile(p1);
        if (!Files.exists(p2)) Files.createFile(p2);
        if (!Files.exists(p3)) Files.createFile(p3);

        findRepository(targetDir);
    }

    public void findRepository() {
        findRepository(this.rootSearchPath != null ? this.rootSearchPath : Path.of("."));
    }

    public void findRepository(Path startPath){
        Path current = startPath.toAbsolutePath().normalize();
        while(current != null && !Files.isDirectory(current.resolve(".ngit"))){
            current = current.getParent();
        }
        if (current == null) {
            return;
        }
        this.repoRoot = current;
        this.ngit = repoRoot.resolve(".ngit");
        this.objects = ngit.resolve("objects");
        this.refs = ngit.resolve("refs");
        this.index = ngit.resolve("index.txt");
        this.HEAD = ngit.resolve("HEAD.txt");
    }
}
