package org.wilson.world.coderule;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.CodeRuleManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class CodeRuleQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoCodeRuleQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("code_rule.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return CodeRuleManager.getInstance().getCodeRuleQuizPairs();
    }
}
