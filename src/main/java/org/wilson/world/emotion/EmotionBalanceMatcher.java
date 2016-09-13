package org.wilson.world.emotion;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class EmotionBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        EmotionQuiz quiz = (EmotionQuiz) QuizDataManager.getInstance().getQuizOfClass(EmotionQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/emotion_quiz_paper.jsp?id=" + quiz.getId();
    }

}
