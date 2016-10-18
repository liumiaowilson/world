package org.wilson.world.sign;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class SignBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return SignQuiz.class;
    }

}
