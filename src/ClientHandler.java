import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientHandler implements Runnable {
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    private Socket socket;
    private ArrayList<ClientHandler> clients;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = this.bufferedReader.readLine();
            String welcome = "Server : " + name + " is now connected";
            System.out.println(welcome);
            this.sendMessage(welcome);
            this.sendListOfUsers();
        } catch (IOException e) {
            this.close();
        }
    }

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

    public void sendMessage(String message) {
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

    public void sendMessageToOneClient(ClientHandler client, String message) {
        try {
            client.bufferedWriter.write(message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String message = bufferedReader.readLine();
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
