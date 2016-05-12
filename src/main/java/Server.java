import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanev_000 on 5.04.2016.
 */
public class Server implements Runnable{

    private Socket clientSocket;
    private List<Socket> sList;
    private final Object monitor = new Object();

    public Server(Socket clientSocket, List<Socket> sList) {
        this.clientSocket = clientSocket;
        this.sList = sList;
    }

    @Override
    public void run() {
        System.out.println("Successfully connected to new client. Opening connection for new possible client..");
        synchronized (monitor){sList.add(clientSocket);}
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())){
            while (!clientSocket.isClosed()){
                String buf = dis.readUTF();
                dos.writeUTF(buf);
                if (buf.equals("Close")){
                    synchronized (monitor){sList.remove(clientSocket);}
                    clientSocket.close();
                    System.out.println("Connection is closed.");
                    break;
                }
            }
        } catch (IOException IOe){
            try {
                synchronized (monitor){sList.remove(clientSocket);}
                clientSocket.close();
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            throw new RuntimeException(IOe);
        }
    }

    public static void main(String[] args) throws IOException{
        try (ServerSocket serverSocket = new ServerSocket(1337)) {
            serverSocket.setSoTimeout(5000);
            List<Socket> sList = new ArrayList<>();
            int i = 0;
            while (!serverSocket.isClosed()){
                try {
                    if (sList.isEmpty() && i > 5){
                        break;
                    }
                    Socket clientSocket = serverSocket.accept();
                    Thread newServer = new Thread(new Server(clientSocket, sList));
                    newServer.start();
                } catch (SocketTimeoutException e){
                    System.out.println("New connection timeout. Trying to reconnect..");
                    if (sList.isEmpty()){
                        i++;
                    }
                }
            }
        }
    }
}
