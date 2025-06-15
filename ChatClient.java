import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ChatClient {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private JTextField nameField;
    private JButton connectButton;
    private JButton sendButton;
    private String username;
    private DefaultListModel<String> userListModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatClient().createGUI();
            }
        });
    }

    public void createGUI() {
        frame = new JFrame("Simple Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout(10, 10));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // User list panel
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));
        frame.add(userScroll, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectButton.addActionListener(new ConnectButtonListener());
        topPanel.add(new JLabel("Username:"));
        topPanel.add(nameField);
        topPanel.add(connectButton);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageField.setEnabled(false);
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setEnabled(false);
        sendButton.addActionListener(new SendButtonListener());
        messageField.addActionListener(new SendButtonListener());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void connectToServer() {
        username = nameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            socket = new Socket("127.0.0.1", 5000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(username);
            nameField.setEditable(false);
            connectButton.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            Thread readerThread = new Thread(new IncomingReader());
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Unable to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            writer.println(message);
            messageField.setText("");
        }
    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("MSG:")) {
                        final String chatMsg = message.substring(4);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                chatArea.append(chatMsg + "\n");
                            }
                        });
                    } else if (message.startsWith("USERS:")) {
                        final String[] users = message.substring(6).split(",");
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                userListModel.clear();
                                for (String user : users) {
                                    if (!user.isEmpty()) {
                                        userListModel.addElement(user);
                                    }
                                }
                            }
                        });
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class ConnectButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            connectToServer();
        }
    }

    class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }
}
