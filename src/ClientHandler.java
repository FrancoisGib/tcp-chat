import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The ClientHandler class represents a thread that handles communication with a client.
 */
public class ClientHandler implements Runnable {
    // Class variables
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    private Socket socket;
    private ArrayList<ClientHandler> clients;

    /**
     * Constructs a new ClientHandler object.
     *
     * @param socket  the client's socket
     * @param clients the list of connected clients
     */
    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = this.bufferedReader.readLine();
            this.sendMessage("Server : " + name + " is now connected");
            this.sendListOfUsers();
        } catch (IOException e) {
            this.close();
        }
    }

    /**
     * Removes the client from the list of connected clients and closes the input and output streams and the socket.
     */
    public void close() {
        this.clients.remove(this);
        try {
            this.bufferedReader.close();
            this.bufferedWriter.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to all connected clients, except the sender.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        System.out.println(message);
        try {
            for (ClientHandler client : this.clients) {
                if (client != this) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the list of currently connected users to the client.
     */
    public void sendListOfUsers() {
        if (clients.size() > 0) {
            String users = "List of users : ";
            for (ClientHandler client : clients) {
                users += client.name + "  ";
            }
            try {
                this.bufferedWriter.write(users);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message to a specific client.
     *
     * @param client  the client to whom the message is to be sent
     * @param message the message to be sent
     */
    public void sendMessageToOneClient(ClientHandler client, String message) {
        try {
            client.bufferedWriter.write(message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The run method is executed when the thread starts.
     * It continuously reads messages from the client and sends them to the appropriate recipient(s).
     * If a message is directed to a specific client, it is sent only to that client.
     * Otherwise, it is broadcasted to all connected clients.
     */
    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String message = bufferedReader.readLine();
                if (message == null) {
                    this.sendMessage("User " + this.name + " disconnect");
                    this.close();
                    return;
                }
                if (message.contains("->")) {
                    String[] tab = message.split("->");
                    message = tab[0].trim();
                    String name = tab[1].trim();
                    Iterator<ClientHandler> it = this.clients.iterator();
                    boolean found = false;
                    while (it.hasNext() && !found) {
                        ClientHandler client = it.next();
                        if (client.name.equals(name)) {
                            this.sendMessageToOneClient(client, message);
                            found = true;
                        }
                    }
                } else
                    this.sendMessage(message);
            }
        } catch (IOException e) {
            this.close();
        }
    }
}
