package nackademin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import nackademin.Game;

public class Client extends Application implements Runnable {

    private int clientID;
    private String selected;

    private Controller controller;
    private Game game;
    private Stage stage;

    private Socket socket;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private DataInputStream dataIn;
    boolean wait = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            this.stage = stage;
            Parent panel;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
            panel = loader.load();
            Scene scene = new Scene(panel);

            controller = loader.getController();
            controller.setClient(this);

            stage.setScene(scene);
            stage.show();

            Thread thread = new Thread(this);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 12345);
            objectIn = new ObjectInputStream(socket.getInputStream());
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());

            clientID = dataIn.readInt();

            /* TODO fix crash that happens when second player connects too fast */
            game = (Game) objectIn.readObject();

            Platform.runLater(() -> {
                stage.setTitle("Quiz Game - Player #" + clientID);
                controller.setGame(game, clientID);
                controller.populate();
            });

            //while(!wait) {System.out.println(wait);}
            game = (Game) objectIn.readObject();

            System.out.println(game.getScore1() + " jek " + game.getScore2());

            Platform.runLater(() -> {
                controller.showCorrectAnswer();
                controller.updateScore(game.getScore1(), game.getScore2());
            });

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void sendGame() {
        try {
            if (clientID == 1) {
                System.out.println("[CLIENT] Player #1 selected " + game.getSelected1() + ".");
                objectOut.writeObject(game);
                objectOut.flush();
            } else if (clientID == 2) {
                System.out.println("[CLIENT] Player #2 selected " + game.getSelected2() + ".");
                objectOut.writeObject(game);
                objectOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

}
