package org.wilson.world.word;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class WordBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return WordQuiz.class;
    }

}
