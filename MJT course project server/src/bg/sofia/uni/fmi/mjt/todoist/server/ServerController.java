package bg.sofia.uni.fmi.mjt.todoist.server;

import java.util.Scanner;

public class ServerController implements Runnable {
    private static final String CONTROLLER_THREAD_NAME = "Controller Thread";
    private static final String START_SERVER_CMD = "start";
    private static final String SHUTDOWN_SERVER_CMD = "shutdown";
    private static final String EXIT_CMD = "exit controller";
    private static final String UNKNOWN_CMD_MESSAGE = "Unknown command";
    private static final String EXIT_MESSAGE = "Controller shutdown";
    private TodoistServer server;

    public ServerController(TodoistServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(CONTROLLER_THREAD_NAME);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals(SHUTDOWN_SERVER_CMD)) {
                    server.stop();
                } else if (command.equals(START_SERVER_CMD) && !server.running()) {
                    ServerRunner runner = new ServerRunner(server);
                    Thread serverThread = Thread.ofPlatform().start(runner);
                } else if (command.equals(EXIT_CMD)) {
                    if (server.running()) {
                        server.stop();
                    }
                    System.out.println(EXIT_MESSAGE);
                    break;
                } else {
                    System.out.println(UNKNOWN_CMD_MESSAGE);
                }
            }
        }
    }
}
