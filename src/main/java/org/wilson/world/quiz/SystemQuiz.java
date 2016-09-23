package org.wilson.world.quiz;

import java.util.List;

public class SystemQuiz implements Quiz {
    private int id;
    private String name;
    private String description;
    private List<QuizItem> items;
    private QuizProcessor processor;
    
    public List<QuizItem> getItems() {
        return items;
    }

    public void setItems(List<QuizItem> items) {
        this.items = items;
    }

    public QuizProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(QuizProcessor processor) {
        this.processor = processor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<QuizItem> getQuizItems() {
        return this.items;
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        return this.processor.process(paper);
    }

    @Override
    public void init() {
    }

    public boolean isPublic() {
        return false;
    }
}
