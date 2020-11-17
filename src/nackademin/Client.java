package nackademin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client extends Application implements Runnable {

    private int clientID;
    private String selected;

    private Controller controller;
    private Question question;
    private Stage stage;

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private ObjectInputStream objectIn;


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
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());

            clientID = dataIn.readInt();

            System.out.println("[CLIENT] Connected to server as Player #" + clientID + ".");

            question = null;
            question = (Question) objectIn.readObject();
            System.out.println("[CLIENT] Question: " + question.getQuestion());

            Platform.runLater(() -> {
                stage.setTitle("Quiz Game - Player #" + clientID);
                controller.populate(question);
            });

            while (true) {
                if (dataIn.readBoolean()) {
                    break;
                }
            }
            Platform.runLater(() -> controller.showCorrectAnswer());
            System.out.println("[CLIENT] Round completed. Correct answer is displayed.");

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            socket.close();
            System.out.println("[CLIENT] Connection closed.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendSelected(String selected) {
        try {
            dataOut.writeUTF(selected);
            dataOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setSelected(String selected) {
        this.selected = selected;
        sendSelected(selected);
    }

}
