package nackademin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final static int PORT = 12345;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(1000);
    private LinkedList<GameServer> gameServers = new LinkedList<>();
    private Game game;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            gameServers.add(new GameServer());

            System.out.println("[SERVER] Waiting for connections...");
            while (true) {
                Socket socket = serverSocket.accept();
                if (gameServers.getLast().clients.size() >= 2) {
                    gameServers.add(new GameServer());
                }
                gameServers.getLast().acceptConnections(socket);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    public class GameServer {
        private ArrayList<ClientHandler> clients = new ArrayList<>();
        private Socket socket;



        void acceptConnections(Socket socket) {
            this.socket = socket;
            ClientHandler clientHandler = new ClientHandler(socket, (clients.size() + 1));
            clients.add(clientHandler);
            executorService.execute(clientHandler);
            System.out.println("[SERVER] Player #" + clients.size() + " has connected.");

            game = new Game();
            System.out.println("[SERVER] 2/2 players.");

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
                    while (clients.size() < 2) {
                    }
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
}