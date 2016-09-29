package org.wilson.world.trickrule;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class TrickRuleBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return TrickRuleQuiz.class;
    }

}
