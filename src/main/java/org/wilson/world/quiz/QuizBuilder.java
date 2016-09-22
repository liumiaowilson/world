package org.wilson.world.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.manager.DiceManager;

public abstract class QuizBuilder {
    private QuizItemMode mode = QuizItemMode.Single;
    @SuppressWarnings("rawtypes")
    private List targets;
    private int size = 5;
    private QuizBuilderStrategy strategy = QuizBuilderStrategy.ShowEither;
    
    public QuizBuilder setSize(int size) {
        this.size = size;
        return this;
    }
    
    @SuppressWarnings("rawtypes")
    public QuizBuilder setTargets(List targets) {
        this.targets = targets;
        return this;
    }
    
    public QuizBuilder setQuizItemMode(QuizItemMode mode) {
        this.mode = mode;
        return this;
    }
    
    public QuizBuilder setStrategy(QuizBuilderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    @SuppressWarnings("rawtypes")
    private QuizItem buildQuizItem(List targets) {
        int options = 4;
        if(this.targets.size() < options) {
            options = this.targets.size();
        }
        
        Collections.shuffle(this.targets);
        QuizItem item = new QuizItem();
        Object first = this.targets.get(0);
        item.id = this.getId(first);
        item.mode = this.mode;
        QuizBuilderStrategy strategy = this.strategy;
        if(QuizBuilderStrategy.ShowEither == strategy) {
            if(DiceManager.getInstance().dice(50)) {
                strategy = QuizBuilderStrategy.ShowTop;
            }
            else {
                strategy = QuizBuilderStrategy.ShowBottom;
            }
        }
        
        if(QuizBuilderStrategy.ShowTop == strategy) {
            item.name = this.getTop(first);
            item.question = item.name;
            String answer = this.getBottom(first);
            for(int i = 0; i < options; i++) {
                Object target = this.targets.get(i);
                QuizItemOption option = new QuizItemOption();
                option.id = 1 + i;
                option.name = this.getBottom(target);
                option.answer = option.name;
                option.url = this.getUrl(target);
                option.value = answer.equals(option.answer) ? 1 : 0;
                item.options.add(option);
            }
            Collections.shuffle(item.options);
        }
        else if(QuizBuilderStrategy.ShowBottom == strategy) {
            item.name = this.getBottom(first);
            item.question = item.name;
            String answer = this.getTop(first);
            for(int i = 0; i < options; i++) {
                Object target = this.targets.get(i);
                QuizItemOption option = new QuizItemOption();
                option.id = 1 + i;
                option.name = this.getTop(target);
                option.answer = option.name;
                option.url = this.getUrl(target);
                option.value = answer.equals(option.answer) ? 1 : 0;
                item.options.add(option);
            }
            Collections.shuffle(item.options);
        }
        
        return item;
    }
    
    public List<QuizItem> build() {
        List<QuizItem> items = new ArrayList<QuizItem>();
        if(this.targets == null || this.targets.isEmpty()) {
            return items;
        }
        
        for(int i = 0; i < this.size; i++) {
            QuizItem item = this.buildQuizItem(items);
            items.add(item);
        }
        
        return items;
    }
    
    public abstract int getId(Object target);
    
    public abstract String getTop(Object target);
    
    public abstract String getBottom(Object target);
    
    public abstract String getUrl(Object target);
}
