package bg.sofia.uni.fmi.mjt.todoist.server;

public class ServerRunner implements Runnable {
    private static final String SERVER_THREAD_NAME = "Server Thread";
    private final TodoistServer server;

    public ServerRunner(TodoistServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(SERVER_THREAD_NAME);
        server.start();
    }
}
