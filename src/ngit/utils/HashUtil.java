package ngit.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String generateHash (byte[] contents)throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashed_content = md.digest(contents);

        StringBuilder generated_hash_hex = new StringBuilder();
        for (byte b : hashed_content){
            generated_hash_hex.append(String.format("%02x", b));
        }
        return generated_hash_hex.toString();
    }
}
