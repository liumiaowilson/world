package org.wilson.world.quiz;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.DiceManager;

public class ComplexQuizPairQuizBuilder extends QuizPairQuizBuilder {
    private List<List<QuizPair>> targetsList = new ArrayList<List<QuizPair>>();
    
    public ComplexQuizPairQuizBuilder addTargets(List<QuizPair> targets) {
        if(targets != null) {
            targetsList.add(targets);
        }
        return this;
    }
    
    public ComplexQuizPairQuizBuilder addMultipleTargets(List<List<QuizPair>> multipleTargets) {
        if(multipleTargets != null) {
            targetsList.addAll(multipleTargets);
        }
        return this;
    }
    
    private List<QuizPair> getRandomList() {
        int n = DiceManager.getInstance().random(targetsList.size());
        return targetsList.get(n);
    }

    @Override
    public List<QuizItem> build() {
        List<QuizItem> items = new ArrayList<QuizItem>();
        if(this.targetsList.isEmpty()) {
            return super.build();
        }
        
        for(int i = 0; i < this.getSize(); i++) {
            List<QuizPair> targets = this.getRandomList();
            QuizItem item = this.buildQuizItem(targets);
            items.add(item);
        }
        
        return items;
    }
}
