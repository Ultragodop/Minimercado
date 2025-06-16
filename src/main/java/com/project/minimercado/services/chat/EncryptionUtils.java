package com.project.minimercado.services.chat;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGO = "AES";

    //  La clave debe ser de 16 bytes (128 bits)
    private static final String SECRET_KEY = "1234567890123456";

    public static String encrypt(String message) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Error al encriptar", ex);
        }
    }

    public static String decrypt(String encryptedMessage) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException("Error al desencriptar", ex);
        }
    }
}
