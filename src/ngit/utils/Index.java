package ngit.utils;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Index {
    private HashMap<Path, String> map = new HashMap<>();
    private Repository repo;

    public Index(Repository repo){
        this.repo = repo;
    }

    public void put(Path path, String hash){
        map.put(path, hash);
    }

    public void load() throws IOException {
        List<String> lines = Files.readAllLines(repo.getIndex());
        for (String line : lines){
            String[] pair = line.split(",");
            map.put(Path.of(pair[0]), pair[1]);
        }
    }

    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Path key : map.keySet()){
            lines.add(key +","+ map.get(key));
        }
        Files.write(repo.getIndex(), lines);
    }
}
