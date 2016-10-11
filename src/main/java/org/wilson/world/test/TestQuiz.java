package org.wilson.world.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.SystemQuiz;
import org.wilson.world.util.IOUtils;

public abstract class TestQuiz extends SystemQuiz {
    private static final Logger logger = Logger.getLogger(TestQuiz.class);
    
    public TestQuiz() {
        List<QuizItem> items = this.loadQuizItems();
        if(items != null) {
            this.setItems(items);
        }
    }
    
    private List<QuizItem> loadQuizItems() {
        List<QuizItem> ret = null;
        InputStream in = null;
        try {
            String file = this.getQuizJSONFile();
            in = this.getClass().getClassLoader().getResourceAsStream(file);
            String json = IOUtils.toString(in);
            ret = QuizDataManager.toQuizItems(json);
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        
        return ret;
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        Quiz quiz = paper.getQuiz();
        List<QuizItem> items = quiz.getQuizItems();
        
        Map<Integer, Integer> score = new HashMap<Integer, Integer>();
        for(QuizItem item : items) {
            int selection = paper.getSelection(item.id);
            QuizItemOption option = item.getOptionById(selection);
            if(option != null) {
                score.put(item.id, option.value);
            }
        }
        
        TestQuizData data = new TestQuizData(score);
        return this.processData(data);
    }
    
    @Override
    public boolean isPublic() {
        return true;
    }
    
    public abstract String getQuizJSONFile();
    
    public abstract QuizResult processData(TestQuizData data);
}
