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

    private boolean count1 = false;
    private boolean count2 = false;

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
        private String selected;


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

                while(clients.size() < 2) {}
                for (ClientHandler c : clients) {
                    c.objectOut.writeObject(game);
                    c.dataOut.flush();
                }

                while (count1 == false || count2 == false) {
                    System.out.println("count1" + count1);
                    System.out.println("count2" + count2);
                    if (clientID == 1) {
                        game.setSelected1(((Game) objectIn.readObject()).getSelected1());
                        System.out.println("[SERVER] Player 1 picked " + game.getSelected1());
                        count1 = true;
                    } else if (clientID == 2) {
                        game.setSelected2(((Game) objectIn.readObject()).getSelected2());
                        System.out.println("[SERVER] Player 2 picked " + game.getSelected2());
                        count2 = true;
                    }

                    System.out.println("count1" + count1);
                    System.out.println("count2" + count2);
                    System.out.println();
                }

                game.setSelected2(((Game) objectIn.readObject()).getSelected2());

                System.out.println(game.getSelected1()+ "   " + game.getSelected2());
            } catch (IOException | NullPointerException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }




}