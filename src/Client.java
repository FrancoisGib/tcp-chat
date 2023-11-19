import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Represents a client that connects to a server and sends/receives messages.
 */
public class Client {
    /**
     * The port number used for the client connection.
     */
    private static int PORT = 1500;

    /**
     * The name of the client.
     */
    private String name;

    /**
     * Represents a socket connection.
     */
    private Socket socket;

    /**
     * A class for reading text from a character-input stream.
     * This class provides methods for reading lines of text from a character-input stream in a buffered manner.
     */
    private BufferedReader bufferedReader;

    /**
     * The BufferedWriter class writes text to a character-output stream, buffering characters so as to provide for the efficient writing of single characters, arrays, and strings.
     */
    private BufferedWriter bufferedWriter;

    /**
     * Represents a client that connects to a server using a socket.
     */
    public Client(String name) {
        this.name = name;
        try {
            this.socket = new Socket("localhost", PORT);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.send(this.name);
            this.listen();
            this.readInput();
        } catch (Exception e) {
            this.close();
        }
    }

    /* Read the messages the client wants to send */
    public void readInput() {
        Scanner sc = new Scanner(System.in);
        while (this.socket.isConnected()) {
            String message = sc.nextLine();
            this.send(this.name + " : " + message);
        }
        sc.close();
    }

    /* Send a message and flush the buffer */
    public void send(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            this.close();
        }
    }

    /* read messages when in buffer */
    public void listen() {
        new Thread(new Runnable() {
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String message = bufferedReader.readLine();
                        if (message == null) {
                            System.out.println("Server stopped");
                            close();
                        }
                        System.out.println(message);
                    } catch (IOException e) {
                        System.out.println("Error");
                        close();
                    }
                }
            }
        }).start();
    }

    /* End the connection */
    public void close() {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
            System.exit(0);
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        if (args.length < 1)
            System.out.println("Enter a username");
        else
            new Client(args[0]);
    }
}