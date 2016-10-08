package org.wilson.world.faceread;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class FaceReadBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return FaceReadQuiz.class;
    }

}
