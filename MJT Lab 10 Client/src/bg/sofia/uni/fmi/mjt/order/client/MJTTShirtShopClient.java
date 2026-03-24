package bg.sofia.uni.fmi.mjt.order.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MJTTShirtShopClient {
    private static final int SERVER_PORT = 4444;

    public static void main(String... args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("TShirt shop client thread " + socket.getLocalPort());

            while (true) {
                System.out.println("Enter command: ");
                String requestMessage = scanner.nextLine();

                if (requestMessage.equals("disconnect")) {
                    System.out.println("Disconnected from the server");
                    break;
                }

                writer.println(requestMessage);
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
