package ngit.utils;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Index {
    private HashMap<String, String> map = new HashMap<>();
    private Repository repo;

    Index(Repository repo){
        this.repo = repo;
    }

    public void put(String hash, String path){
        map.put(path, hash);
    }

    public void load() throws IOException {
        List<String> lines = Files.readAllLines(repo.getIndex());
        for (String line : lines){
            String[] pair = line.split(",");
            map.put(pair[0], pair[1]);
        }
    }

    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (String key : map.keySet()){
            lines.add(key +","+ map.get(key));
        }
        Files.write(repo.getIndex(), lines);
    }
}
