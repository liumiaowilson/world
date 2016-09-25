package org.wilson.world.writingskill;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.WritingSkillManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class WritingSkillQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoWritingSkillQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("writing_skill.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return WritingSkillManager.getInstance().getWritingSkillQuizPairs();
    }
}
