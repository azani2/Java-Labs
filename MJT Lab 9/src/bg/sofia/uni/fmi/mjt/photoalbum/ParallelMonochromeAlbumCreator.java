package bg.sofia.uni.fmi.mjt.photoalbum;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private LinkedBlockingQueue<Image> imagesQueue;
    private final int imageProcessorsCount;
    private final Image eod;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imagesQueue = new LinkedBlockingQueue<>();
        this.imageProcessorsCount = imageProcessorsCount;
        this.eod = new Image("endReached", new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY));
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        Thread producer = new Thread(new Producer(imagesQueue, sourceDirectory, eod, imageProcessorsCount));
        producer.start();
        System.out.println("Producer started working.");

        if (!Files.isDirectory(Path.of(outputDirectory))) {
            try {
                System.out.println(("create? " + Files.createDirectory(Path.of(outputDirectory))));
                System.out.println("Created directory " + outputDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < imageProcessorsCount; i++) {
            Thread consumer = new Thread(new Consumer(imagesQueue, outputDirectory, eod));
            consumer.start();
            System.out.println("Consumer No." + (1 + i) + " started working.");
        }

    }
}
