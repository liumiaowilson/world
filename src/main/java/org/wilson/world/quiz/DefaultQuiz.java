package org.wilson.world.quiz;

import java.util.List;

import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.model.QuizData;

public class DefaultQuiz implements Quiz {
    private QuizData data;
    private QuizProcessor processor;
    private List<QuizItem> items;
    
    public DefaultQuiz(QuizData data, QuizProcessor processor) {
        this.data = data;
        this.processor = processor;
    }
    
    @Override
    public int getId() {
        return this.data.id;
    }

    @Override
    public void setId(int id) {
    }

    @Override
    public String getName() {
        return this.data.name;
    }

    @Override
    public String getDescription() {
        return this.data.description;
    }

    @Override
    public List<QuizItem> getQuizItems() {
        if(this.items == null) {
            this.items = QuizDataManager.toQuizItems(this.data.content);
        }
        
        return this.items;
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        return this.processor.process(paper);
    }

    @Override
    public void init() {
    }

}
