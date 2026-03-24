package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        final int year = 2020;
        final String allRocketsFile = "all-rockets-from-1957.csv";
        final String allMissionsFile = "all-missions-from-1957.csv";
        final String encryptionAlgorithm = "AES";
        final int keySizeInBits = 128;
        SecretKey secretKey;

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionAlgorithm);
            keyGenerator.init(keySizeInBits);
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (Reader rocketsFile = new FileReader(allRocketsFile);
             Reader missionsFile = new FileReader(allMissionsFile);
             OutputStream rocketOutputStream = new FileOutputStream("encrypted-most-reliable-rocket.txt")) {
            MJTSpaceScanner scanner = new MJTSpaceScanner(missionsFile, rocketsFile, secretKey);
            LocalDate from = LocalDate.of(year, 1, 1);
            LocalDate to = LocalDate.of(1 + year, 1, 1);
            scanner.saveMostReliableRocket(rocketOutputStream, from, to);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when saving most reliable rocket.");
        } catch (CipherException e) {
            throw new RuntimeException(e);
        }
    }
}