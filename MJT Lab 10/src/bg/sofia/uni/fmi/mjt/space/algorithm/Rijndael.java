package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;

    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (var reader = new InputStreamReader(inputStream);
                 var encryptedOutputStream = new CipherOutputStream(outputStream, cipher)) {
                StringBuilder message = new StringBuilder();

                int byteRead;
                while ((byteRead = reader.read()) != -1) {
                    message.append((char) byteRead);
                }
                encryptedOutputStream.write(message.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new CipherException("An error occurred when reading an writing data to file.");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CipherException("An error occurred when encrypting data.");
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (var encryptedReader = new InputStreamReader(inputStream);
                 var decryptedOutputStream = new CipherOutputStream(outputStream, cipher)) {
                StringBuilder message = new StringBuilder();

                int byteRead;
                while ((byteRead = encryptedReader.read()) != -1) {
                    message.append((char) byteRead);
                }
                decryptedOutputStream.write(message.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new CipherException("An error occurred when reading an writing data to file.");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CipherException("An error occurred when decrypting data.");
        }
    }
}
