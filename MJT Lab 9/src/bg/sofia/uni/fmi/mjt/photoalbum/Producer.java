package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements Runnable {
    private final LinkedBlockingQueue<Image> imagesQueue;
    private final Path sourceDirectory;
    private final Image eod;
    private final int consumersCount;

    public Producer(LinkedBlockingQueue<Image> imagesQueue, String sourceDirectory, Image eod, int consumersCount) {
        this.imagesQueue = imagesQueue;
        this.sourceDirectory = Path.of(sourceDirectory);
        this.eod = eod;
        this.consumersCount = consumersCount;
    }

    private Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    @Override
    public void run() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDirectory, "*.png")) {
            for (Path imagePath : stream) {
                imagesQueue.put(loadImage(imagePath));
                System.out.println("Put " + imagePath.toString() + " to queue.\n" +
                    "Images Queue: " + imagesQueue.toString());
            }

            for (int i = 0; i < consumersCount; i++) {
                imagesQueue.put(eod);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
