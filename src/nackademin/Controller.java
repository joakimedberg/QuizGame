package nackademin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private Button pos1;
    @FXML
    private Button pos2;
    @FXML
    private Button pos3;
    @FXML
    private Button pos4;
    @FXML
    private Label question;
    @FXML
    private Label score1;
    @FXML
    private Label score2;
    @FXML
    private Label player1;
    @FXML
    private Label player2;

   private List<String> answers;
   private Game game;
   private String selected;
   private int clientID;
   private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        score1.setText("0");
        score2.setText("0");

        question.setText("WAITING FOR PLAYER #2 TO CONNECT.");

    }

    public void setGame(Game game, int clientID) {
        this.clientID = clientID;
        this.game = game;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void populate() {

        answers = game.getAnswers();
        question.setText(game.getQuestion());
        pos1.setText(answers.get(0));
        pos2.setText(answers.get(1));
        pos3.setText(answers.get(2));
        pos4.setText(answers.get(3));


    }

    @FXML
    private void chosenTile(ActionEvent event) {
        String color = "-fx-background-color: #e5e500; ";
        selected = ((Button) event.getSource()).getId();

        if (clientID == 1) {
            game.setSelected1(((Button) event.getSource()).getText());
        } else if (clientID == 2) {
            game.setSelected2(((Button) event.getSource()).getText());
        }

        ((Button) event.getSource()).setStyle(color);

        pos1.setDisable(true);
        pos2.setDisable(true);
        pos3.setDisable(true);
        pos4.setDisable(true);

        client.sendGame();


    }

    public void showCorrectAnswer() {
        String color = "-fx-background-color: #00e500; ";
        if (pos1.getText().equals(game.getAnswer())) {
            pos1.setStyle(color);
        } else if (pos2.getText().equals(game.getAnswer())) {
            pos2.setStyle(color);
        } else if (pos3.getText().equals(game.getAnswer())) {
            pos3.setStyle(color);
        } else if (pos4.getText().equals(game.getAnswer())) {
            pos4.setStyle(color);
        }
    }

}
