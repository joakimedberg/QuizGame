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


    public Server() {
        try {

            serverSocket = new ServerSocket(PORT);

            gameServers.add(new GameServer());
            System.out.println("[SERVER] Waiting for connections...");
            while (true) {
                Socket socket = serverSocket.accept();
                if (gameServers.getLast().clients.size() >= 2) {
                    gameServers.add(new GameServer());
                }// if (gameServers.getLast().clients.size() < 2) {
                gameServers.getLast().acceptConnections(socket);
                // }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    private class GameServer {
        private ArrayList<ClientHandler> clients = new ArrayList<>();
        private Game game = new Game();
        int serverNr;

        void acceptConnections(Socket socket) {
            ClientHandler clientHandler = new ClientHandler(socket, (clients.size() + 1));
            clients.add(clientHandler);
            executorService.execute(clientHandler);
            serverNr = gameServers.indexOf(gameServers.getLast());
            System.out.println("[GAME SERVER #" + serverNr + "] Player #" + clients.size() + " has connected.");
            if (clients.size() == 2) {
                System.out.println("[GAME SERVER #" + serverNr + "] 2/2 players. Launching game.");
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

                    while (clients.size() < 2) { }

                    // send Game object
                    for (ClientHandler c : clients) {
                        c.objectOut.reset();
                        c.objectOut.writeObject(game);
                        c.objectOut.flush();
                    }

                    while (game.getPlayer1().equals("#1") || game.getPlayer2().equals("#2")) {
                        if (clientID == 1) {
                            game.setPlayer1(((Game) clients.get(0).objectIn.readObject()).getPlayer1());
                            System.out.println("[GAME SERVER #" + serverNr + "] Player 1 name is " + game.getPlayer1());
                        } else if (clientID == 2) {
                            game.setPlayer2(((Game) clients.get(1).objectIn.readObject()).getPlayer2());
                            System.out.println("[GAME SERVER #" + serverNr + "] Player 2 name is " + game.getPlayer2());
                        }
                    }

                    // send Game object
                    for (ClientHandler c : clients) {
                        c.objectOut.reset();
                        c.objectOut.writeObject(game);
                        c.objectOut.flush();
                    }

                    // receive selected answers from clients
                    for (ClientHandler c : clients) {
                        if (c.clientID == 1) {
                            game.setSelected1(((Game) clients.get(0).objectIn.readObject()).getSelected1());
                            System.out.println("[GAME SERVER #" + serverNr + "] Player 1 picked " + game.getSelected1());
                        } else if (c.clientID == 2) {
                            game.setSelected2(((Game) clients.get(1).objectIn.readObject()).getSelected2());
                            System.out.println("[GAME SERVER #" + serverNr + "] Player 2 picked " + game.getSelected2());
                        }
                    }

                    game.gradeAnswers();
                    System.out.println("[GAME SERVER #" + serverNr + "] The answers from the players are graded.");

                    for (ClientHandler c : clients) {
                        c.objectOut.reset();
                        c.objectOut.writeObject(game);
                        c.objectOut.flush();
                    }
                    System.out.println("[GAME SERVER #" + serverNr + "] Points are set.");

                } catch (IOException | NullPointerException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}