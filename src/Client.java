import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BigInteger publicKey;
    private BigInteger modulus;

    public void startClient() {

        String host;
        int port;

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter server ip");
        host = sc.nextLine();

        System.out.println("Enter port");
        port = sc.nextInt();

        try {
            this.socket = new Socket(host, port);
            new Thread(new HeartBeat()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connected to " + host + ":" + port);
        System.out.println("Your ip: " + Util.getLocalIp() + '\n');

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = null;
        try {

            BufferedReader socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            publicKey = new BigInteger(socketReader.readLine());
            modulus = new BigInteger(socketReader.readLine());

            bw = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), Charset.forName("UTF-8")));

            String message, encrypted;
            while ((message = br.readLine()) != null) {

                encrypted = RSA.encrypt(message, publicKey, modulus);
                System.out.println("\nEncrypted: " + encrypted + '\n');

                bw.write("#message#" + encrypted);
                bw.newLine();
                bw.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class HeartBeat implements Runnable {


        @Override
        public void run() {
            try {

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    dos.writeUTF("#alive#\n");
                    dos.flush();
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                System.out.println("Program terminated");
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
