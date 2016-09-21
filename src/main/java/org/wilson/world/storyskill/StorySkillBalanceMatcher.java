package org.wilson.world.storyskill;

import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public class StorySkillBalanceMatcher extends DefaultBalanceMatcher {

    @Override
    protected String getMenuURI(MenuItem item) {
        StorySkillQuiz quiz = (StorySkillQuiz) QuizDataManager.getInstance().getQuizOfClass(StorySkillQuiz.class);
        if(quiz == null) {
            return null;
        }
        
        return "/jsp/quiz_paper.jsp?id=" + quiz.getId();
    }

}
