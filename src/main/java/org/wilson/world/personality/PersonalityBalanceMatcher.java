package org.wilson.world.personality;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class PersonalityBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        PersonalityQuiz quiz = (PersonalityQuiz) QuizDataManager.getInstance().getQuizOfClass(PersonalityQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
