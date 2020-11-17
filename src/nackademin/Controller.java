package nackademin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

    private Client client;
    private Question q;
    private ArrayList<String> list;
    private String pick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        score1.setText("0");
        score2.setText("0");
        list = new ArrayList<>(4);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void populate(Question q) {
        this.q = q;

        list.add(q.getAnswer());
        list.add(q.getAlternative1());
        list.add(q.getAlternative2());
        list.add(q.getAlternative3());

        Collections.shuffle(list);

        question.setText(q.getQuestion());
        pos1.setText(list.get(0));
        pos2.setText(list.get(1));
        pos3.setText(list.get(2));
        pos4.setText(list.get(3));


    }

    @FXML
    private void chosenTile(ActionEvent event) {
        String color = "-fx-background-color: #e5e500; ";
        pick = ((Button) event.getSource()).getId();

        client.setSelected(((Button) event.getSource()).getText());

        ((Button) event.getSource()).setStyle(color);

        pos1.setDisable(true);
        pos2.setDisable(true);
        pos3.setDisable(true);
        pos4.setDisable(true);

    }

    public void showCorrectAnswer() {
        String color = "-fx-background-color: #00e500; ";
        if (pos1.getText().equals(q.getAnswer())) {
            pos1.setStyle(color);
        } else if (pos2.getText().equals(q.getAnswer())) {
            pos2.setStyle(color);
        } else if (pos3.getText().equals(q.getAnswer())) {
            pos3.setStyle(color);
        } else if (pos4.getText().equals(q.getAnswer())) {
            pos4.setStyle(color);
        }
    }

}
