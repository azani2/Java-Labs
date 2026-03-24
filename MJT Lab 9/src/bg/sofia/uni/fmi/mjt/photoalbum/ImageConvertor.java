package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.awt.image.BufferedImage;

public class ImageConvertor {
    private final Image image;
    private static final String EXTENSION = "png";
    private final Path destinationDirectory;

    public ImageConvertor(Image image, Path destinationDirectory) {
        this.image = image;
        this.destinationDirectory = destinationDirectory.toAbsolutePath();
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(image.data.getWidth(),
            image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    private Path getOutPath(Image toSave) {
        return Path.of(destinationDirectory.getFileName() +
            System.getProperty("file.separator") +
            toSave.name + "." + EXTENSION).toAbsolutePath();
    }

    public void convert() {
        Image converted = convertToBlackAndWhite(image);

        try {
            System.out.println("path? " + getOutPath(converted).toFile());
            System.out.println("wrote? " + ImageIO.write(converted.data, EXTENSION, getOutPath(converted).toFile()));
        } catch (IOException e) {
            System.out.println("Encountered a problem when writing file.");
        }
    }
}
