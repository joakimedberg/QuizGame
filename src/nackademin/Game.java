package nackademin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game implements Serializable {

    private String player1;
    private String player2;
    private int score1;
    private int score2;
    private Question qa;
    private String selected1;
    private String selected2;
    private ArrayList<String> answers;

    public Game() {
        Database db = new Database();
        qa = db.getQuestions().get(0);
        answers = new ArrayList<>(4);

        setPlayer1();
        setPlayer2();

        score1 = 0;
        score2 = 0;

    }

    public String getQuestion() {
        return qa.getQuestion();
    }
    public String getAnswer() {
        return qa.getAnswer();
    }
    public String getSelected1(){
        return selected1;
    }
    public String getSelected2(){
        return selected2;
    }
    public List<String> getAnswers() {
        shuffleAnswers();
        return answers;
    }
    public int getScore1 () {
        return score1;
    }
    public int getScore2 () {
        return score2;
    }

    private void setPlayer1() {
        player1 = "#1";
    }
    private void setPlayer2() {
        player2 = "#2";
    }
    public void setSelected1(String selected) {
        selected1 = selected;
    }
    public void setSelected2(String selected) {
        selected2 = selected;
    }

    private void shuffleAnswers() {
        answers.add(qa.getAnswer());
        answers.add(qa.getAlternative1());
        answers.add(qa.getAlternative2());
        answers.add(qa.getAlternative3());

        Collections.shuffle(answers);
    }
    public void gradeAnswers() {
        if (getSelected1().equals(getAnswer())) {
            score1++;
        }
        if (getSelected2().equals(getAnswer())) {
            score2++;
        }
    }
}
