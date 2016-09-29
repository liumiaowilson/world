package org.wilson.world.trickrule;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.TrickRuleManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class TrickRuleQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoTrickRuleQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("trick_rule.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return TrickRuleManager.getInstance().getTrickRuleQuizPairs();
    }
}
