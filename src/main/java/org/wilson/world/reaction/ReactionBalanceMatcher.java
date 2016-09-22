package org.wilson.world.reaction;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class ReactionBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        ReactionQuiz quiz = (ReactionQuiz) QuizDataManager.getInstance().getQuizOfClass(ReactionQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
