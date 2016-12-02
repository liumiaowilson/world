package org.wilson.world.quiz;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.RewardManager;
import org.wilson.world.reward.Reward;

public abstract class QuizPairQuiz extends SystemQuiz {
    
    public abstract EventType getEventType();
    
    public abstract int getQuizSize();
    
    public abstract List<QuizPair> getQuizPairs();

    @Override
    public QuizResult doProcess(QuizPaper paper) {
        QuizResult result = new MultipleContainsQuizResultBuilder(paper).build();
        int total = (Integer)result.data.get("total");
        int sum = (Integer)result.data.get("sum");
        
        double ratio = sum * 1.0 / total;
        if(ratio >= 0.6) {
            int exp = ExpManager.getInstance().getExp();
            exp += 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of " + this.getName());
        }
        if(total == sum) {
        	int max = ConfigManager.getInstance().getConfigAsInt("perfect_quiz.reward.max", 5);
        	Reward reward = RewardManager.getInstance().generateReward(max);
        	if(reward != null) {
        		RewardManager.getInstance().deliver(reward);
        	}
        }
        
        Event event = new Event();
        event.type = this.getEventType();
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = this.getQuizSize();
        List<QuizPair> pairs = this.getQuizPairs();
        
        QuizBuilder builder = new QuizPairQuizBuilder().setSize(maxLength).setTargets(pairs);
        
        this.setItems(builder.build());
    }

    @Override
    public boolean isPublic() {
        return true;
    }
}
