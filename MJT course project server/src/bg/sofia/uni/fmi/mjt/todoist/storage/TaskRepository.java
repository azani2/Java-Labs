package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private final Path saveFile;
    private ConcurrentHashMap<Task, UserId> allTasks;
    private ConcurrentHashMap<UserId, UserSpecificStorage> usersToTasks;
    private static final String CSV_SEPARATOR = ",";
    private static final String COMPLETED_PREFIX = "COMPLETED ";

    public TaskRepository(String saveFileName) {
        this.saveFile = Path.of(saveFileName);
        allTasks = new ConcurrentHashMap<>();
        usersToTasks = new ConcurrentHashMap<>();
        loadTasksFromSaveFile();
        organiseTasks();
    }

    private void initSaveFile() {
        if (Files.notExists(saveFile)) {
            try {
                Files.createFile(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void loadTasksFromSaveFile() {
        initSaveFile();

        allTasks = new ConcurrentHashMap<>();
        try (var br = Files.newBufferedReader(saveFile)) {
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String[] tokens = line.split(CSV_SEPARATOR);
                String username = tokens[tokens.length - 1];
                UserId userId = new UserId(username);
                Task task = Task.fromCSVLine(line);
                allTasks.putIfAbsent(task, userId);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void organiseTasks() {
        for (Task task : allTasks.keySet()) {
            UserId user = allTasks.get(task);
            if (usersToTasks.containsKey(user)) {
                UserSpecificStorage userTasks = usersToTasks.get(user);
                userTasks.addTask(task);
                usersToTasks.replace(user, userTasks);
            } else {
                UserSpecificStorage userStorage = new UserSpecificStorage();
                userStorage.addTask(task);
                usersToTasks.putIfAbsent(user, userStorage);
            }
        }
    }

    private synchronized void writeTaskUserEntry(FileWriter writer, Task task, UserId userId)
            throws  IOException {
        writer.write(task.toString());
        writer.write(CSV_SEPARATOR);
        writer.write(userId.username());
        if (!allTasks.isEmpty()) {
            writer.write(System.lineSeparator());
        }
        writer.flush();
    }

    private synchronized void saveTaskAndUserId(Task task, UserId userId) {
        try (var writer = new FileWriter(saveFile.toFile(), true)) {
            writeTaskUserEntry(writer, task, userId);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void saveAllTasksAndUserIds() {
        try (var writer = new FileWriter(saveFile.toFile(), false)) {
            for (Task task : allTasks.keySet()) {
                UserId userId = allTasks.get(task);
                writeTaskUserEntry(writer, task, userId);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean addTask(UserId userId, Task task) {
        if (!usersToTasks.containsKey(userId)) {
            usersToTasks.putIfAbsent(userId, new UserSpecificStorage());
        }

        if (!allTasks.containsKey(task)) {
            allTasks.putIfAbsent(task, userId);
            saveTaskAndUserId(task, userId);
        }

        return usersToTasks.get(userId).addTask(task);
    }

    private synchronized  void deleteFromAllTasks(Task task) {
        Task toRemove = null;
        for (Task t : allTasks.keySet()) {
            if (t.name().equals(task.name())) {
                toRemove = t;
                break;
            }
        }
        if (toRemove != null) {
            allTasks.remove(toRemove);
        }
    }

    public boolean updateTask(UserId userId, Task task) {
        if (usersToTasks.containsKey(userId)) {
            Task updated = usersToTasks.get(userId).updateTask(task);

            if (updated != null) {
                deleteFromAllTasks(task);
                allTasks.putIfAbsent(updated, userId);
                saveAllTasksAndUserIds();
                return true;
            }
        }
        return false;
    }

    public boolean deleteTask(UserId userId, Task task) {
        if (!usersToTasks.containsKey(userId)) {
            return false;
        }

        deleteFromAllTasks(task);
        saveAllTasksAndUserIds();
        return usersToTasks.get(userId).deleteTask(task);
    }

    public Task getTask(UserId userId, Task task) {
        if (usersToTasks.containsKey(userId) &&
            usersToTasks.get(userId) != null) {
            return usersToTasks.get(userId).getTask(task);
        }
        return null;
    }

    public Set<Task> getUserInbox(UserId userId) {
        if (usersToTasks.containsKey(userId)) {
            Set<Task> userTasks = usersToTasks.get(userId).getInbox();
            if (userTasks != null) {
                return Collections.unmodifiableSet(userTasks);
            }
        }
        return null;
    }

    public Set<Task> getUserCompleted(UserId userId) {
        if (usersToTasks.containsKey(userId)) {
            Set<Task> userTasks = usersToTasks.get(userId).getCompleted();
            if (userTasks != null) {
                return Collections.unmodifiableSet(userTasks);
            }
        }
        return null;
    }

    public Set<Task> getUserTasksOnDate(UserId userId, LocalDate date) {
        if (usersToTasks.containsKey(userId)) {
            Set<Task> userTasks = usersToTasks.get(userId).getTasksOnDate(date);
            if (userTasks != null) {
                return Collections.unmodifiableSet(userTasks);
            }
        }
        return null;
    }

    public boolean finishTask(UserId userId, Task task) {
        if (usersToTasks.containsKey(userId) &&
                usersToTasks.get(userId) != null) {
            Task actual = getTask(userId, task);
            Task completedTask = new Task(COMPLETED_PREFIX + actual.name(),
                    actual.date(), actual.dueDate(), actual.description());
            deleteFromAllTasks(actual);
            allTasks.putIfAbsent(completedTask, userId);
            saveAllTasksAndUserIds();
            return usersToTasks.get(userId).finishTask(task);
        }
        return false;
    }

}
