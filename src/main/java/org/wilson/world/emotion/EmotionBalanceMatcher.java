package org.wilson.world.emotion;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class EmotionBalanceMatcher extends QuizBalanceMatcher {

    @Override
    protected String getQuizPaperPage() {
        return "/jsp/emotion_quiz_paper.jsp";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return EmotionQuiz.class;
    }

}
