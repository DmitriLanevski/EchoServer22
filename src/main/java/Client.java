import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by lanev_000 on 3.03.2016.
 */
public class Client {
    public static void main(String[] args){
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 1337);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             BufferedReader bufIn = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Echo client opened.");
            String userInput;
            String echo;
            while(true){
                userInput = bufIn.readLine();
                dos.writeUTF(userInput);
                echo = dis.readUTF();
                System.out.println(echo);
                if (echo.equals("Close")){
                    throw new IOException("Client closed.");
                }
            }
        } catch(SocketException SEe){
            System.out.println("Connection was closed due server shut down.");
        } catch (IOException IOe) {
            System.out.println(IOe);
        }
    }
}