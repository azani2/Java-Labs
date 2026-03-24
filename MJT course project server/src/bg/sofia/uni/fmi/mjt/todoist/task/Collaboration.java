package bg.sofia.uni.fmi.mjt.todoist.task;

import bg.sofia.uni.fmi.mjt.todoist.CollaborationFileFormatException;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;
import bg.sofia.uni.fmi.mjt.todoist.validation.StringValidator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Collaboration {
    private Path file;
    private String name;
    private Map<UserId, Set<Task>> usersToTasks;
    private UserId creator;

    private static final String FILE_READ_ERROR_MESSAGE = "There was a problem when reading from file.";
    private static final String FILE_WRITE_ERROR_MESSAGE = "There was a problem when writing to file ";
    private static final String FILE_FORMAT_ERROR_MESSAGE = "The file to read resources from is " +
            "not in the correct format.";
    private static final String CSV_SEPARATOR = ",";
    private static final String NAME_ARG = "Name ";
    private static final int HEADER_ELEMENTS_COUNT = 2;
    private static final int ARG_COLLAB_NAME_LEN = 7;
    private static final int ARG_DESC_LEN = 14;
    private static final String ARG_DESC = "--description=";

    public Collaboration(String fileName, String name,
                         UserId creator, Map<UserId, Set<Task>> usersToTasks) {
        if (fileName != null && !fileName.isEmpty()) {
            this.file = Path.of(fileName);
        }
        this.name = name;
        this.creator = creator;
        this.usersToTasks = usersToTasks;
    }

    private static void printErrorMessages(String message, Exception e) {
        System.out.println(message);
        System.err.println(message);
        e.printStackTrace();
    }

    public static synchronized Collaboration fromFile(Path file) {
        Map<UserId, Set<Task>> usersTasks = new HashMap<>();
        try (var reader = Files.newBufferedReader(file)) {
            String line  = reader.readLine().replace(System.lineSeparator(), "");
            String[] tokens = line.split(CSV_SEPARATOR);
            if (tokens.length < HEADER_ELEMENTS_COUNT) {
                try {
                    throw new CollaborationFileFormatException(FILE_FORMAT_ERROR_MESSAGE);
                } catch (Exception e) {
                    printErrorMessages(FILE_FORMAT_ERROR_MESSAGE, e);
                }
            }
            while ((line = reader.readLine()) != null) {
                int userNameEndIdx = line.indexOf(CSV_SEPARATOR);
                UserId currentUser = new UserId(line.substring(0, userNameEndIdx));
                Task currentTask = Task.fromCSVLine(line.substring(userNameEndIdx + 1));
                if (!usersTasks.containsKey(currentUser)) {
                    Set<Task> tasks = new HashSet<>();
                    tasks.add(currentTask);
                    usersTasks.putIfAbsent(currentUser, tasks);
                } else {
                    usersTasks.get(currentUser).add(currentTask);
                }
            }
            return new Collaboration(file.getFileName().toString(), tokens[0], new UserId(tokens[1]), usersTasks);
        } catch (IOException e) {
            printErrorMessages(FILE_READ_ERROR_MESSAGE + file.getFileName(), e);
        }
        return null;
    }

    public static Collaboration fromCommandArgs(String[] args, UserId creator) {
        args[args.length - 1] = args[args.length - 1].replace(System.lineSeparator(), "");
        String collabName = args[0].substring(ARG_COLLAB_NAME_LEN);
        StringValidator.assertNotNullOrEmpty(collabName, NAME_ARG);
        return new Collaboration(null, collabName, creator, new HashMap<>());
    }

    private synchronized void initEmptySaveFile() {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            Files.createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private synchronized void writeToSaveFile() {
        initEmptySaveFile();
        try (var wr = new FileWriter(file.toFile(), true)) {
            String header = this.name + CSV_SEPARATOR + creator.username() + System.lineSeparator();
            wr.write(header);

            for (UserId userId : usersToTasks.keySet()) {
                String line = userId.username() + CSV_SEPARATOR +
                        usersToTasks.get(userId).toString() + System.lineSeparator();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void save() {
        writeToSaveFile();
    }

    public Set<UserId> getUsers() {
        Set<UserId> userIdSet = new HashSet<>(usersToTasks.keySet());
        return Collections.unmodifiableSet(userIdSet);
    }

    public String getName() {
        return name;
    }

    public Map<UserId, Set<Task>> getUsersToTasks() {
        if (usersToTasks == null || usersToTasks.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(usersToTasks);
    }

    public Set<Task> getTasks() {
        Set<Task> taskSet = new HashSet<>();
        for (var entry : usersToTasks.entrySet()) {
            taskSet.addAll(entry.getValue());
        }
        return Collections.unmodifiableSet(taskSet);
    }

    public UserId getCreator() {
        return creator;
    }

    public boolean assignTo(Task task, UserId userId) {
        if (task == null) {
            throw new IllegalArgumentException();
        }

        if (!usersToTasks.containsKey(userId)) {
            Set<Task> newTaskSet = new HashSet<>();
            newTaskSet.add(task);
            usersToTasks.putIfAbsent(userId, newTaskSet);
            return true;
        }
        return usersToTasks.get(userId).add(task);
    }

}
