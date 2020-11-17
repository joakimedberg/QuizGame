package nackademin;

import java.io.Serializable;

public class Question implements Serializable {

    private String category, question, answer, alt1, alt2, alt3;

    public Question(String category, String question, String answer, String alt1, String alt2, String alt3) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.alt1 = alt1;
        this.alt2 = alt2;
        this.alt3 = alt3;

    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAlternative1() {
        return alt1;
    }

    public String getAlternative2() {
        return alt2;
    }

    public String getAlternative3() {
        return alt3;
    }

    public String getCategory() {
        return category;
    }
}
