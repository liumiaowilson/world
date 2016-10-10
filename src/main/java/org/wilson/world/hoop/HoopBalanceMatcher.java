package org.wilson.world.hoop;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class HoopBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return HoopQuiz.class;
    }

}
