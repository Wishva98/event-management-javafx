package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Hashing256 {
    public static String encryptPassword(String password) {
        try {
            //get hashing algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //hash password
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
