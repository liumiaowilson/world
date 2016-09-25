package org.wilson.world.quiz;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.balance.DefaultBalanceMatcher;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.menu.MenuItem;

public abstract class QuizBalanceMatcher extends DefaultBalanceMatcher {
    @SuppressWarnings("rawtypes")
    public abstract Class getQuizClass();
    
    protected boolean match(HttpServletRequest request, String uri) {
        if(StringUtils.isBlank(uri)) {
            return false;
        }
        
        String str = request.getRequestURI() + "?" + request.getQueryString();
        return str.startsWith(uri) && !str.contains("action=next");
    }
    
    protected String getQuizPaperPage() {
        return "/jsp/quiz_paper.jsp";
    }
    
    @Override
    protected String getMenuURI(MenuItem item) {
        Quiz quiz = QuizDataManager.getInstance().getQuizOfClass(this.getQuizClass());
        if(quiz == null) {
            return null;
        }
        
        return this.getQuizPaperPage() + "?id=" + quiz.getId();
    }

}
