import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * @author khali
 */
public class Server implements Runnable {

    //Create an arraylist of clients
    public static ArrayList<BufferedWriter> clients = new ArrayList<>();
    private final Socket socket;

    public Server(Socket socket) {
        //Initialize the socket
        this.socket = socket;
    }

    public static void main(String[] args) throws Exception {
        //Create a server socket
        ServerSocket serverSocket = new ServerSocket(2003);
        //noinspection InfiniteLoopStatement
        while (true) {
            //Accept a new connection
            Socket socket = serverSocket.accept();
            Server server1 = new Server(socket);
            //Create a new thread for the connection
            Thread thread = new Thread(server1);
            //Start the thread
            thread.start();
            System.out.println("Client Connected");
        }
    }

    //Run the server and listen for connections
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clients.add(writer);
            //noinspection InfiniteLoopStatement
            while (true) {
                String line = reader.readLine();
                //
                for (BufferedWriter client : clients) {
                    try {
                        client.write(line + "\n");
                        client.flush();
                    } catch (Exception e) {
                        System.out.println("Connection lost");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Client Disconnected");
        }

    }

}
