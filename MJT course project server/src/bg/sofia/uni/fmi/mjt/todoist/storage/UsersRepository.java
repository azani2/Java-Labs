package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.user.User;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;
import bg.sofia.uni.fmi.mjt.todoist.validation.StringValidator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class UsersRepository {
    private static final String INIT_FILE_ERROR_MESSAGE = "Failed to create read file.";
    private static final String READ_FROM_FILE_ERROR = "An error occurred when reading from file.";
    private static final String WRITE_TO_FILE_ERROR = "An error occurred when writing to file.";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists.";
    private final Path saveFile;
    private CopyOnWriteArrayList<User> users;

    private static final String USERNAME_ARG = "Username";
    private static final String PASSWORD_ARG = "Password";
    private static final String FILE_NAME_ARG = "File name";

    public UsersRepository(String saveDestination) {
        StringValidator.assertNotNullOrEmpty(saveDestination, FILE_NAME_ARG);

        this.saveFile = Path.of(saveDestination);
        loadUsersFromSaveFile();
    }

    public UsersRepository(Collection<User> users, String saveDestination) {
        this.saveFile = Path.of(saveDestination);
        loadUsersFromSaveFile();
        for (User u : users) {
            addUser(u);
        }
    }

    private void initSaveFile() {
        if (Files.notExists(saveFile)) {
            try {
                Files.createFile(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(INIT_FILE_ERROR_MESSAGE);
            }
        }
    }

    private void loadUsersFromSaveFile() {
        users = new CopyOnWriteArrayList<>();
        initSaveFile();

        try (var br = Files.newBufferedReader(saveFile)) {
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                users.addIfAbsent(User.fromLine(line));
            }
        } catch (IOException e) {
            System.out.println(READ_FROM_FILE_ERROR);
            e.printStackTrace();
        }
    }

    private void writeUser(Writer wr, User user) throws IOException {
        wr.write(user.toString());
        if (!users.isEmpty()) {
            wr.write(System.lineSeparator());
        }
        wr.flush();
    }

    private synchronized void saveAll() {
        try (var writer = new FileWriter(saveFile.toFile(), true)) {
            for (User user : users) {
                writeUser(writer, user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private synchronized void saveUser(User user) {
        try (var writer = new FileWriter(saveFile.toFile(), true)) {
            writeUser(writer, user);
        } catch (IOException e) {
            System.out.println(WRITE_TO_FILE_ERROR);
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        StringValidator.assertNotNullOrEmpty(username, USERNAME_ARG);

        boolean foundUser = false;
        for (User user : users) {
            if (user.userId().username().equals(username)) {
                foundUser = true;
                break;
            }
        }
        return foundUser;
    }

    public boolean hasUserWith(String username, String password) {
        StringValidator.assertNotNullOrEmpty(username, USERNAME_ARG);
        StringValidator.assertNotNullOrEmpty(password, PASSWORD_ARG);

        boolean foundUser = false;
        for (User user : users) {
            if (user.userId().username().equals(username) &&
                user.password().equals(password)) {
                foundUser = true;
                break;
            }
        }
        return foundUser;
    }

    public void addUser(User user) {
        if (userExists(user.userId().username())) {
            System.err.println(USER_ALREADY_EXISTS_MESSAGE);
            throw new IllegalArgumentException(USER_ALREADY_EXISTS_MESSAGE);
        }
        users.addIfAbsent(user);
        saveUser(user);
    }

    public Set<UserId> getUserIds() {
        if (users.isEmpty()) {
            return null;
        }
        return users.stream()
                .map(User::userId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<User> getUsersData() {
        if (users.isEmpty()) {
            return null;
        }
        return users.stream().collect(Collectors.toUnmodifiableSet());
    }
}
