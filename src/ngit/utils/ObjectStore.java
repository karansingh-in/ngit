package ngit.utils;

import ngit.repository.Repository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class ObjectStore {
    public static String store(byte[] data) throws NoSuchAlgorithmException, IOException {
        String hash = HashUtil.generateHash(data);
        Repository repo = new Repository();
        repo.findRepository();
        Path path = Path.of(repo.getObjects().toString() + "/" + hash);

        if (!Files.exists(path)) {
            byte[] compressed = CompressUtil.compress(data);
            Files.write(path, compressed);
        }
        return hash;
    }
}
