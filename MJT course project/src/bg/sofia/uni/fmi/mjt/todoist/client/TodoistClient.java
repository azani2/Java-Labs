package bg.sofia.uni.fmi.mjt.todoist.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class TodoistClient {
    private static final int SERVER_PORT = 4444;
    private static final String SERVER_NAME = "localhost";
    private static final String ERROR_FILE_NAME = "errClient.txt";
    private static final int WRONG_CREDENTIALS_STATUS = 401;
    private static final String EXIT_PROMPT = "exit-pr";
    private static final String USERNAME_PROMPT = "Enter username:";
    private static final String PASSWORD_PROMPT = "Enter password:";
    private static final String INVALID_CREDENTIALS_REQUEST = "inv";
    private static final String LOGIN_REQUEST_PREFIX = "login";
    private static final String REGISTER_REQUEST_PREFIX = "register";
    private static final String DEFAULT_SEPARATOR = " ";
    private static final String INVALID_COMMAND_MESSAGE = "Invalid command";
    private static final String COMMAND_PROMPT = "Please enter command and arguments: ";
    private static final int STTUS_CODE_LEN = 4;
    private static final String EXIT_CLIENT = "exit";
    private static final String INVALID_LOGIN = "Invalid username or password. Please try again.";
    private static final String LOGIN_PROMPT = "Login or register to begin using the client";
    private static final String EXIT_PROMPT_INFO = "To exit %s prompt, type \"exit-pr\"";
    private static final String ERROR_MESSAGE = "There is a problem with network communication. " +
            "Please contact system administrator and check logs in " + ERROR_FILE_NAME;
    private static final String INFO_FILE_NAME = "info.txt";
    private static final Path INFO_FILE = Path.of(INFO_FILE_NAME);
    private static final String FAILED_TO_LOAD_INFO_MESSAGE = "Failed to lead client instructions from info file." +
            "If you need help, please see \"" + INFO_FILE_NAME + "\" and retry, or check eroor logs in " +
            ERROR_FILE_NAME + ".";

    private static void redirectSystemErr(Path file) throws IOException {
        PrintStream fileStream = new PrintStream(Files.newOutputStream(file));
        System.setErr(fileStream);
    }

    private static String constructInitSessionRequest(String username, String password, String requestType) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return INVALID_CREDENTIALS_REQUEST;
        }
        return requestType + DEFAULT_SEPARATOR + username + DEFAULT_SEPARATOR + password;
    }

    private static String addUsernamePrefix(String username, String command) {
        return username + DEFAULT_SEPARATOR + command;
    }

    private static void printInfo() {
        try (var br = Files.newBufferedReader(INFO_FILE)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(FAILED_TO_LOAD_INFO_MESSAGE);
            e.printStackTrace();
        }
    }

    private static String attemptInitSessionWith(String requestType,
                                               Scanner scanner, PrintWriter writer, BufferedReader reader)
            throws IOException {
        String username = "";
        String password;
        String reply = "";
        String deniedStatus = String.valueOf(WRONG_CREDENTIALS_STATUS);
        String responseStatus = deniedStatus;
        while (responseStatus.equals(deniedStatus)) {
            System.out.printf((EXIT_PROMPT_INFO) + "%n", requestType);
            System.out.println(USERNAME_PROMPT);
            username = scanner.nextLine();
            System.out.println(PASSWORD_PROMPT);
            password = scanner.nextLine();
            if (username.equals(EXIT_PROMPT) || password.equals(EXIT_PROMPT)) {
                return null;
            }
            String loginRequest = (constructInitSessionRequest(username, password, requestType));
            if (loginRequest.equals(INVALID_CREDENTIALS_REQUEST)) {
                continue;
            }
            writer.println(addUsernamePrefix(username, loginRequest));
            reply = reader.readLine();
            responseStatus = reply.split(DEFAULT_SEPARATOR)[0];
            if (responseStatus.equals(deniedStatus)) {
                System.out.println(reply.substring(STTUS_CODE_LEN));
                System.out.println(INVALID_LOGIN);
            }
        }
        System.out.println(reply);
        return username;
    }

    public static void main(String... args) throws IOException {
        redirectSystemErr(Path.of(ERROR_FILE_NAME));
        printInfo();
        try (SocketChannel socketChannel = SocketChannel.open();
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_NAME, SERVER_PORT));

            String currentWorkingUser = null;
            while (currentWorkingUser == null) {
                System.out.println(LOGIN_PROMPT);
                String command = scanner.nextLine();

                switch (command) {
                    case LOGIN_REQUEST_PREFIX ->
                            currentWorkingUser = attemptInitSessionWith(LOGIN_REQUEST_PREFIX, scanner, writer, reader);
                    case REGISTER_REQUEST_PREFIX ->
                            currentWorkingUser = attemptInitSessionWith(REGISTER_REQUEST_PREFIX,
                                    scanner, writer, reader);
                    case EXIT_CLIENT -> {
                        return;
                    }
                    default -> System.out.println(INVALID_COMMAND_MESSAGE);
                }
            }

            String command = "";
            while (true) {
                System.out.println(COMMAND_PROMPT);
                command = scanner.nextLine();

                if (command.equals(LOGIN_REQUEST_PREFIX) || command.equals(REGISTER_REQUEST_PREFIX)) {
                    System.out.println(INVALID_COMMAND_MESSAGE);
                    continue;
                }

                if (command.equals(EXIT_CLIENT)) {
                    break;
                }

                writer.println(addUsernamePrefix(currentWorkingUser, command));

                String reply ;
                while ((reply = reader.readLine()) != null) {
                    if (reply.isEmpty()) {
                        break;
                    }
                    System.out.println(reply);
                }
            }

        } catch (IOException e) {
            System.out.println(ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
