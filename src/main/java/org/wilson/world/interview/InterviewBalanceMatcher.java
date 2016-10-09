package org.wilson.world.interview;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class InterviewBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return InterviewQuiz.class;
    }

}
