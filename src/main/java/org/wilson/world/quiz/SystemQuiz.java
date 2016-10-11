package org.wilson.world.quiz;

import java.util.List;

public class SystemQuiz implements Quiz {
    private int id;
    private String name;
    private String description;
    private List<QuizItem> items;
    private QuizProcessor processor;
    
    public SystemQuiz() {
        String name = this.getClass().getSimpleName();
        if(name.endsWith("Quiz")) {
            name = name.substring(0, name.length() - 4);
        }
        
        this.setName(name + " Quiz");
        this.setDescription("A quiz for testing " + name);
    }
    
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
        if(paper.isProcessed()) {
            return null;
        }
        else {
            paper.setProcessed(true);
            return this.doProcess(paper);
        }
    }
    
    protected QuizResult doProcess(QuizPaper paper) {
        return this.processor.process(paper);
    }

    @Override
    public void init() {
    }

    public boolean isPublic() {
        return false;
    }
}
