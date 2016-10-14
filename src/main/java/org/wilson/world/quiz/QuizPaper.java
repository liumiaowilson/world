package org.wilson.world.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizPaper {
    private Quiz quiz;
    private Map<Integer, List<Integer>> selectedMap = new HashMap<Integer, List<Integer>>();
    
    private int current = -1;
    private boolean processed = false;
    
    public QuizPaper(Quiz quiz) {
        this.quiz = quiz;
        this.quiz.init();
    }
    
    public Quiz getQuiz() {
        return this.quiz;
    }
    
    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void select(int itemId, int... optionIds) {
        List<Integer> selection = this.selectedMap.get(itemId);
        if(selection == null) {
            selection = new ArrayList<Integer>();
            this.selectedMap.put(itemId, selection);
        }
        
        selection.clear();
        for(int optionId : optionIds) {
            selection.add(optionId);
        }
    }
    
    public List<Integer> getSelections(int itemId) {
        List<Integer> selection = this.selectedMap.get(itemId);
        if(selection == null) {
            return Collections.emptyList();
        }
        return selection;
    }
    
    public int getSelection(int itemId) {
        List<Integer> selection = this.selectedMap.get(itemId);
        if(selection == null || selection.isEmpty()) {
            return 0;
        }
        else {
            return selection.get(0);
        }
    }
    
    public QuizItem current() {
        List<QuizItem> items = this.quiz.getQuizItems();
        if(this.current < 0 || this.current >= items.size()) {
            return null;
        }
        
        return items.get(this.current);
    }
    
    public QuizItem next() {
        List<QuizItem> items = this.quiz.getQuizItems();
        if(items == null || items.isEmpty()) {
            return null;
        }
        
        if(this.current < 0) {
            this.current = 0;
            return items.get(0);
        }
        
        if(this.current >= items.size()) {
            return null;
        }
        
        QuizItem item = items.get(this.current);
        List<Integer> selection = this.selectedMap.get(item.id);
        if(selection != null && !selection.isEmpty()) {
            for(QuizItemOption option : item.options) {
                if(selection.contains(option.id)) {
                    if(option.next != 0) {
                        int pos = -1;
                        for(int i = 0; i < items.size(); i++) {
                            QuizItem qi = items.get(i);
                            if(qi.id == option.next) {
                                pos = i;
                                break;
                            }
                        }
                        if(pos >= 0) {
                            this.current = pos;
                            return items.get(this.current);
                        }
                    }
                }
            }
        }
        
        this.current += 1;
        if(this.current < items.size()) {
            return items.get(this.current);
        }
        else {
            return null;
        }
    }
    
    public String getStatus() {
        if(this.current < 0) {
            return "NotStarted";
        }
        else if(this.current >= this.quiz.getQuizItems().size()) {
            return "Finished";
        }
        else {
            return "InProgress";
        }
    }
    
    public int getNumOfItems() {
        if(this.quiz == null) {
            return 0;
        }
        return this.quiz.getQuizItems().size();
    }
    
    public int getCurrent() {
        return this.current;
    }
}
