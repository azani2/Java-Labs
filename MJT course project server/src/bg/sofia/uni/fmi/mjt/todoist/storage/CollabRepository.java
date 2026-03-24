package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.task.Collaboration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CollabRepository {
    private static AtomicInteger collabId = new AtomicInteger(1);
    private static final String READ_FROM_FILE_ERROR_MESSAGE = "There was a problem when reading from save directory.";
    private static final String FILE_INIT_PROBLEM_MESSAGE = "There was a problem when opening/creating save directory.";
    private static final String COLLAB_FILE_PREFIX = "collab";
    private static final String COLLAB_FILE_SUFFIX = ".txt";
    private CopyOnWriteArrayList<Collaboration> collabs;
    private Path saveDir;

    public CollabRepository(String saveDirDest) {
        try {
            if (!Files.exists(Path.of(saveDirDest))) {
                this.saveDir = Files.createDirectory(Path.of(saveDirDest));
            }
            this.saveDir = Path.of(saveDirDest);
            initCollabs();
        } catch (IOException e) {
            System.out.println(READ_FROM_FILE_ERROR_MESSAGE);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void initSaveDir() {
        if (Files.notExists(saveDir)) {
            try {
                Files.createDirectory(saveDir);
            } catch (IOException e) {
                System.out.println(FILE_INIT_PROBLEM_MESSAGE);
                e.printStackTrace();
            }
        } else if (!Files.isDirectory(saveDir)) {
            try {
                Files.delete(saveDir);
                Files.createDirectory(saveDir);
            } catch (IOException e) {
                System.out.println(FILE_INIT_PROBLEM_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void initCollabs() {
        initSaveDir();
        this.collabs = new CopyOnWriteArrayList<>();

        try (var dirStream = Files.newDirectoryStream(saveDir)) {
            for (Path file : dirStream) {
                collabId.incrementAndGet();
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                collabs.addIfAbsent(Collaboration.fromFile(file));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addCollab(Collaboration c) {
        if (c == null || collabs.contains(c)) {
            return false;
        }
        String fileName = COLLAB_FILE_PREFIX + collabId.incrementAndGet() + COLLAB_FILE_SUFFIX;
        Path saveFile = Path.of(saveDir.getFileName().toString(), fileName);
        Collaboration savedCollab = new Collaboration(saveFile.getFileName().toString(),
                c.getName(), c.getCreator(), c.getUsersToTasks());
        collabs.addIfAbsent(savedCollab);
        savedCollab.save();
        return true;
    }

}
