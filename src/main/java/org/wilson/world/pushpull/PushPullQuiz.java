package org.wilson.world.pushpull;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.PushPullManager;
import org.wilson.world.quiz.MultipleContainsQuizResultBuilder;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;
import org.wilson.world.quiz.QuizPairQuizBuilder;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;

public class PushPullQuiz extends QuizPairQuiz {
    public PushPullQuiz() {
        this.setName("Push Pull Quiz");
        this.setDescription("A quiz for testing the push pulls");
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        QuizResult result = new MultipleContainsQuizResultBuilder(paper).build();
        int total = (Integer)result.data.get("total");
        int sum = (Integer)result.data.get("sum");
        
        double ratio = sum * 1.0 / total;
        if(ratio >= 0.6) {
            int exp = ExpManager.getInstance().getExp();
            exp += 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of push pull quiz");
        }
        
        Event event = new Event();
        event.type = EventType.DoPushPullQuiz;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("push_pull.quiz.max_length", 5);
        List<QuizPair> pairs = PushPullManager.getInstance().getPushPullQuizPairs();
        
        QuizBuilder builder = new QuizPairQuizBuilder().setSize(maxLength).setTargets(pairs);
        
        this.setItems(builder.build());
    }

    @Override
    public EventType getEventType() {
        return EventType.DoPushPullQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("push_pull.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return PushPullManager.getInstance().getPushPullQuizPairs();
    }
}
