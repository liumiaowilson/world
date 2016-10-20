package org.wilson.world.coderule;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class CodeRuleBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return CodeRuleQuiz.class;
    }

}
