import java.util.*;
import java.net.*;
import java.io.*;

public class ChatServer {
    private Map<Socket, PrintWriter> clientWriters = Collections.synchronizedMap(new HashMap<>());
    private Map<Socket, String> clientUsernames = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server listening on port 5000");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.put(clientSocket, writer);
                new Thread(new ClientHandler(clientSocket)).start();
                System.out.println("Got a connection from: " + clientSocket.getInetAddress());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private BufferedReader reader;
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                // Get username
                String username = reader.readLine();
                clientUsernames.put(socket, username);
                broadcast(username + " has joined the chat!");
                updateUserList();

                while ((message = reader.readLine()) != null) {
                    String formattedMessage = username + ": " + message;
                    System.out.println(formattedMessage);
                    broadcast(formattedMessage);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            try {
                String username = clientUsernames.get(socket);
                clientWriters.remove(socket);
                clientUsernames.remove(socket);
                broadcast(username + " has left the chat.");
                updateUserList();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println("MSG:" + message);
            }
        }
    }

    private void updateUserList() {
        StringBuilder userList = new StringBuilder("USERS:");
        synchronized (clientUsernames) {
            for (String user : clientUsernames.values()) {
                userList.append(user).append(",");
            }
        }
        String listMessage = userList.toString();
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(listMessage);
            }
        }
    }
}
