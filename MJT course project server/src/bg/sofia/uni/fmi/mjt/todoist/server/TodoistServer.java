package bg.sofia.uni.fmi.mjt.todoist.server;

import bg.sofia.uni.fmi.mjt.todoist.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.todoist.command.CommandExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class TodoistServer {
    private static final String DEFAULT_ERROR_MESSAGE = "Please check te logs in \"errData.txt\" for more information.";
    private static final int BUFFER_SIZE = 10240;
    private static final String HOST_NAME = "localhost";
    private static final String SERVER_THREAD_NAME = "Server Thread";
    private static final String SERVER_START_MESSAGE = "Server started";
    private static final String SERVER_SHUTDOWN_MESSAGE = "Server shutdown";
    private final CommandExecutor commandExecutor;
    private final int port;
    private boolean isWorking;
    private ByteBuffer buffer;
    private Selector selector;

    public TodoistServer(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        int readyBytes = clientChannel.read(buffer);
        if (readyBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        output = output + System.lineSeparator();
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = socketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void configureServerSocketChannel(ServerSocketChannel channel) throws IOException {
        channel.bind(new InetSocketAddress(HOST_NAME, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() {
        System.out.println(SERVER_START_MESSAGE);
        Thread.currentThread().setName(SERVER_THREAD_NAME);

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isWorking = true;

            while (isWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keySet.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = getClientInput(clientChannel);
                            if (clientInput == null) {
                                continue;
                            }
                            String output = commandExecutor.execute(CommandCreator.newCommand(clientInput));
                            writeClientOutput(clientChannel, output);
                        } else if (key.isAcceptable()) {
                            accept(key);
                        }

                        keyIterator.remove();
                    }
                } catch (Exception e) {
                    System.out.println(DEFAULT_ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
            System.out.println(SERVER_SHUTDOWN_MESSAGE);
        } catch (IOException e) {
            System.out.println(DEFAULT_ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void stop() {
        this.isWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    public boolean running() {
        return isWorking;
    }
}
