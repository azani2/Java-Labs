package bg.sofia.uni.fmi.mjt.todoist;

import bg.sofia.uni.fmi.mjt.todoist.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.todoist.server.ServerController;
import bg.sofia.uni.fmi.mjt.todoist.server.TodoistServer;
import bg.sofia.uni.fmi.mjt.todoist.storage.CollabRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.TaskRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.UsersRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final int SERVER_PORT =  4444;
    private static final String USERS_FILE = "usersData.csv";
    private static final String ERRORS_FILE = "errData.txt";
    private static final String TASKS_FILE = "tasksData.txt";
    private static final String COLLABS_FILE = "collabsData";

    private static void redirectSystemErr(Path file) throws IOException {
        PrintStream fileStream = new PrintStream(Files.newOutputStream(file));
        System.setErr(fileStream);
    }

    public static void main(String[] args)  throws Exception {
        redirectSystemErr(Path.of(ERRORS_FILE));
        UsersRepository userRepository = new UsersRepository(USERS_FILE);
        TaskRepository userData = new TaskRepository(TASKS_FILE);
        CollabRepository collabRepository = new CollabRepository(COLLABS_FILE);
        CommandExecutor executor = new CommandExecutor(userRepository, userData, collabRepository);

        TodoistServer server = new TodoistServer(SERVER_PORT, executor);
        ServerController controller = new ServerController(server);
        controller.run();
    }
}
