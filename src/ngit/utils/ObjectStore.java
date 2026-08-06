package ngit.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class ObjectStore {
    public static String store(byte[] data) throws NoSuchAlgorithmException, IOException {
        String hash = HashUtil.generateHash(data);
        Path path = Path.of("../../.ngit/objects", hash);

        if (!Files.exists(path)) {
            byte[] compressed = CompressUtil.compress(data);
            Files.write(path, compressed);
        }
        return hash;
    }
}
