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
        if (!Files.exists(repo.getIndex())) {
            return;
        }
        List<String> lines = Files.readAllLines(repo.getIndex());
        for (String line : lines){
            line = line.trim();
            if (line.isBlank())
                continue;
            int commaIdx = line.indexOf(",");
            if (commaIdx != -1) {
                map.put(Path.of(line.substring(0, commaIdx).trim()), line.substring(commaIdx + 1).trim());
            }
        }
    }

    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Path key : map.keySet()){
            lines.add(key +","+ map.get(key));
        }
        Files.write(repo.getIndex(), lines);
    }

    public boolean containsFile(Path path){
        return map.containsKey(path);
    }

    public boolean containsHash(Path path, String hash){
        return (map.get(path).equals(hash));
    }
}
