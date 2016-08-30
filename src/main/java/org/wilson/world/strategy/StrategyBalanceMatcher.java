package org.wilson.world.strategy;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class StrategyBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        StrategyQuiz quiz = (StrategyQuiz) QuizDataManager.getInstance().getQuizOfClass(StrategyQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
