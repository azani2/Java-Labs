package bg.sofia.uni.fmi.mjt.photoalbum;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer implements Runnable {
    private final LinkedBlockingQueue<Image> imagesQueue;
    private final Path destinationDirectory;
    private final Image eod;

    public Consumer(LinkedBlockingQueue<Image> imagesQueue, String destinationDirectory, Image eod) {
        this.imagesQueue = imagesQueue;
        this.destinationDirectory = Path.of(destinationDirectory);
        this.eod = eod;
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(image.data.getWidth(),
            image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    @Override
    public void run() {
        ImageConvertor convertor = null;
        try {
            while (true) {
                Image toConvert = imagesQueue.take();
                if (toConvert.name.equals(eod.name)) {
                    break;
                }
                convertor = new ImageConvertor(toConvert, destinationDirectory);
                convertor.convert();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
