package nackademin;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Question> questions = new ArrayList<>();

    public Database() {
        populate();
    }


    private void populate() {
        questions.add(new Question("Rabadab", "Hur många bultar finns det i Ölandsbron?", "7 428 954", "0", "1", "42"));
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
