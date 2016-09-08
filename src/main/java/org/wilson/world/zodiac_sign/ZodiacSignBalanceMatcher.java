package org.wilson.world.zodiac_sign;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class ZodiacSignBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        ZodiacSignQuiz quiz = (ZodiacSignQuiz) QuizDataManager.getInstance().getQuizOfClass(ZodiacSignQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
