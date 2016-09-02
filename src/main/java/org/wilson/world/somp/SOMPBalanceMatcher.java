package org.wilson.world.somp;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class SOMPBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        SOMPQuiz quiz = (SOMPQuiz) QuizDataManager.getInstance().getQuizOfClass(SOMPQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
