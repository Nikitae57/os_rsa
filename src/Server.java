import java.io.*;
import java.security.*;
import java.util.*;
import java.net.*;

public class Server {

    private ArrayList<ClientHandler> clientHandlers;

    public static String publicKey;
    public static String privateKey;
    public static String modulus;

    public void startServer() {

        RSA rsa = new RSA();
        publicKey = rsa.getPublicExponent();
        privateKey = rsa.getPrivateExponent();
        modulus = rsa.getModulus();

        System.out.println("Enter port");
        int port = new Scanner(System.in).nextInt();
        System.out.println("Server started");

        String localIp = Util.getLocalIp();

        System.out.println("Clients can connect to " + localIp + ":" + port + '\n');

        try {

            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket;
            clientHandlers = new ArrayList<ClientHandler>();

            ClientHandler clientHandler;
            while (true) {

                socket = serverSocket.accept();
                clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                System.out.println("\nClient " + socket.getRemoteSocketAddress() + " connected\n");

                new Thread(clientHandler).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
