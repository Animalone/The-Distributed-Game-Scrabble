package ScrabbleServer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

public class Server {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt("12345");
            ServerSocketFactory factory = ServerSocketFactory.getDefault();
            ServerSocket server = factory.createServerSocket(port);
            Map<Integer, ConnectionToClient> clients = new HashMap<Integer, ConnectionToClient>();
            ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();
            String username = null;
            int id = 0;
            System.out.println("Server is up!");

            while (true) {
                Socket client = server.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                InputStreamReader isr = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(isr);
                username = br.readLine();

                if (checkClient(username, clients)) {
                    System.out.println(username + "has already logged in!");
                    output.writeUTF(username + "has already logged in!\n");
                } else {
                    System.out.println(username + " connected");
                    ConnectionToClient newclient = new ConnectionToClient(id, client, username);
                    clients.put(id, newclient);
                    Thread clientThread = new Thread(new HandleClient(newclient, messages));
                    clientThread.start();
                    id += 1;
                }

                if (id >= 2) {
                    Thread gameThread = new Thread(new Game(clients, messages));
                    gameThread.start();
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("\"" + args[0] + "\"" + " is not a valid integer");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkClient(String newclient, Map<Integer, ConnectionToClient> clients) {
        for (ConnectionToClient c : clients.values()) {
            if (c.getUserName().equals(newclient)) {
                return true;
            }
        }
        return false;
    }
}