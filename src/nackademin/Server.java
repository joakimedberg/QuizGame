package nackademin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final static int PORT = 12345;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private int countOfSelected = 0;

    public Server() {

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
    }

    void acceptConnections() {
        try {
            System.out.println("[SERVER] Waiting for connections...");
            while (clients.size() < 2) {
                Socket socket = serverSocket.accept();
                ClientHandler ssc = new ClientHandler(socket, (clients.size() + 1));
                clients.add(ssc);
                executorService.execute(ssc);
                System.out.println("[SERVER] Player #" + clients.size() + " has connected.");
            }
            System.out.println("[SERVER] 2/2 players.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private ObjectOutputStream objectOut;

        private int clientID;
        private String selected;
        private Database db;


        public ClientHandler(Socket socket, int clientID) {
            db = new Database();
            this.socket = socket;
            this.clientID = clientID;

        }

        @Override
        public void run() {
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                objectOut = new ObjectOutputStream(socket.getOutputStream());

                dataOut.writeInt(clientID);
                dataOut.flush();

                objectOut.writeObject(db.getQuestions().get(0));
                objectOut.flush();

                while (countOfSelected < 2) {
                    if (clientID == 1) {
                        receiveSelected();
                        System.out.println("[SERVER] Player 1 picked " + selected);
                        ++countOfSelected;
                    } else if (clientID == 2) {
                        receiveSelected();
                        System.out.println("[SERVER] Player 2 picked " + selected);
                        ++countOfSelected;
                    }
                }
                System.out.println("[SERVER] All players have picked an answer.");

                sendCompleted(true);
                System.out.println("[SERVER] Turn is completed. Showing correct answer.");

            } catch (IOException | NullPointerException ex) {
                ex.printStackTrace();
            }
        }

        private void sendCompleted(boolean bool) throws IOException {
            for (ClientHandler c : clients) {
                c.dataOut.writeBoolean(bool);
                c.dataOut.flush();
            }
        }

        private void receiveSelected() throws IOException {
            selected = dataIn.readUTF();
        }

        private void closeConnection() {
            try {
                socket.close();
                System.out.println("[SERVER] Connection closed.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}