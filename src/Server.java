import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static int PORT = 1500;
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket, this.clients);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            this.close();
        }
    }

    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().run();
    }
}
