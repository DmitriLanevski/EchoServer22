import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by lanev_000 on 5.04.2016.
 */
public class Server implements Runnable{

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        System.out.println("Trying to connect with client.");
        try (Socket clientSocket = serverSocket.accept();
             DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())){
            System.out.println("Successfully connected to new client. Opening connection for new possible client..");
            Thread newServer = new Thread(new Server(serverSocket));
            newServer.start();
            while (true){
                if (!serverSocket.isClosed()){
                    String buf = dis.readUTF();
                    dos.writeUTF(buf);
                    if (buf.equals("Close")){
                        throw new IOException("Connection is closed.");
                    }
                } else {
                    throw new IOException("Connection is closed.");
                }

            }
        } catch (SocketTimeoutException e){
            System.out.println("New connection timeout. Trying to reconnect..");
            Thread newServer = new Thread(new Server(serverSocket));
            newServer.start();
        } catch (SocketException SEe){
            System.out.println("Server was closed. Waiting for last client input..");
        } catch (IOException IOe){
            System.out.println(IOe);
            //System.out.println("Could not accept client.");
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1337);
            serverSocket.setSoTimeout(5000);
            Thread newServer = new Thread(new Server(serverSocket));
            newServer.start();
            String userInput;
            try (BufferedReader bufIn = new BufferedReader(new InputStreamReader(System.in))){
                while(true){
                    userInput = bufIn.readLine();
                    if (userInput.equals("Close")){
                        serverSocket.close();
                        throw new IOException("Server will be closed after all connections close.");
                    }
                }
            }

        } catch (IOException IOe) {
            System.out.println(IOe);
        }
    }

}
