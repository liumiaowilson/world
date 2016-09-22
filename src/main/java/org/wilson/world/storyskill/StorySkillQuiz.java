package org.wilson.world.storyskill;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.StorySkillManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class StorySkillQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoStorySkillQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("story_skill.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return StorySkillManager.getInstance().getStorySkillQuizPairs();
    }
}
