package org.wilson.world.coldread;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class ColdReadBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return ColdReadQuiz.class;
    }

}
