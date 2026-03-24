package bg.sofia.uni.fmi.mjt.photoalbum;

public class Main {
    public static void main(String[] args) {
        System.out.println("За целите на ръчното тестване оставям sout-овете, " +
            "които разказват какво в какъв ред се случва.");

        final int numberOfProcessors = 7;
        ParallelMonochromeAlbumCreator albumMaker = new ParallelMonochromeAlbumCreator(numberOfProcessors);
        albumMaker.processImages("colorImages",
            "blackAndWhiteImages");
    }
}