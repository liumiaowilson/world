package org.wilson.world.reaction;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class ReactionBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return ReactionQuiz.class;
    }

}
