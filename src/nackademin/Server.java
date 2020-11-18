package nackademin;

import java.io.*;
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
    private Game game;


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
                ClientHandler clientHandler = new ClientHandler(socket, (clients.size() + 1));
                clients.add(clientHandler);
                executorService.execute(clientHandler);
                System.out.println("[SERVER] Player #" + clients.size() + " has connected.");
            }
            game = new Game();
            System.out.println("[SERVER] 2/2 players.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {

        private Socket socket;
        private ObjectOutputStream objectOut;
        private ObjectInputStream objectIn;
        private DataOutputStream dataOut;

        private int clientID;

        public ClientHandler(Socket socket, int clientID) {
            this.socket = socket;
            this.clientID = clientID;
        }

        @Override
        public void run() {
            try {
                objectOut = new ObjectOutputStream(socket.getOutputStream());
                dataOut = new DataOutputStream((socket.getOutputStream()));
                objectIn = new ObjectInputStream(socket.getInputStream());

                dataOut.writeInt(clientID);
                dataOut.flush();

                // send Game object
                while(clients.size() < 2) {}
                for (ClientHandler c : clients) {
                    c.objectOut.writeObject(game);
                    c.objectOut.flush();
                }

                // receive selected answers from clients
                for (ClientHandler c : clients) {
                    if (c.clientID == 1) {
                        game.setSelected1(((Game) clients.get(0).objectIn.readObject()).getSelected1());
                        System.out.println("[SERVER] Player 1 picked " + game.getSelected1());
                    } else if (c.clientID == 2) {
                        game.setSelected2(((Game) clients.get(1).objectIn.readObject()).getSelected2());
                        System.out.println("[SERVER] Player 2 picked " + game.getSelected2());
                    }
                }

                game.gradeAnswers();
                System.out.println("[SERVER] The answers from the players are graded.");

                for (ClientHandler c : clients) {
                    c.objectOut.reset();
                    c.objectOut.writeObject(game);
                    c.objectOut.flush();
                }
                System.out.println("[SERVER] Points are set.");

            } catch (IOException | NullPointerException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }




}