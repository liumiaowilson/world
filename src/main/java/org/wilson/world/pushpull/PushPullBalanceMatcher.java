package org.wilson.world.pushpull;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class PushPullBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return PushPullQuiz.class;
    }

}
