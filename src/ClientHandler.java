import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private SocketAddress sAddress;
    private long lastTimeAlive;
    private BigInteger privateKey, modulus;

    public ClientHandler(Socket socket) {
        clientSocket = socket;
        sAddress = socket.getRemoteSocketAddress();
        privateKey = new BigInteger(Server.privateKey);
        modulus = new BigInteger(Server.modulus);
    }


    @Override
    public void run() {
        try {

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                    clientSocket.getInputStream(), Charset.forName("UTF-8"))
            );

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write(Server.publicKey);
            bw.newLine();
            bw.flush();
            System.out.println(Server.publicKey);

            bw.write(Server.modulus);
            bw.newLine();
            bw.flush();

            lastTimeAlive = System.currentTimeMillis();
            new Thread(new HeartBeatChecker()).start();

            String message, decrypted;
            while ((message = bufferedReader.readLine()) != null) {
                lastTimeAlive = System.currentTimeMillis();

                if (message.startsWith("#message#")) {

                    message = message.replace("#message#", "");
                    decrypted = RSA.decrypt(message, privateKey, modulus);

                    System.out.println("Decrypted: " + decrypted);
                }
            }

        } catch (SocketTimeoutException ex) {
            System.out.println("Client " + sAddress + " disconnected");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class HeartBeatChecker implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {

                    if (System.currentTimeMillis() - lastTimeAlive > 5000) {
                        System.out.println("Client " + sAddress + " disconnected");
                        return;
                    }
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
