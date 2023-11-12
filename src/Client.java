import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static int PORT = 1500;

    private String name;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

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
            e.printStackTrace();
        }
    }

    /* read messages when in buffer */
    public void listen() {
        new Thread(new Runnable() {
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String message = bufferedReader.readLine();
                        if (message.equals("quit"))
                            close();
                        else {
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1)
            System.out.println("Enter a username");
        else
            new Client(args[0]);
    }
}