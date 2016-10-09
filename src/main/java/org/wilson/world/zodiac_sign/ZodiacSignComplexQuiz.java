package org.wilson.world.zodiac_sign;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ZodiacSignManager;
import org.wilson.world.quiz.ComplexQuizPairQuiz;
import org.wilson.world.quiz.QuizPair;

public class ZodiacSignComplexQuiz extends ComplexQuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoZSComplexQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("zodiac_sign.quiz.max_length", 5);
    }

    @Override
    public List<List<QuizPair>> getMultipleQuizPairs() {
        List<List<QuizPair>> ret = new ArrayList<List<QuizPair>>();
        
        ret.add(ZodiacSignManager.getInstance().getZodiacSignQuizPairs(QuizType.Date));
        ret.add(ZodiacSignManager.getInstance().getZodiacSignQuizPairs(QuizType.Strengths));
        ret.add(ZodiacSignManager.getInstance().getZodiacSignQuizPairs(QuizType.Weaknesses));
        ret.add(ZodiacSignManager.getInstance().getZodiacSignQuizPairs(QuizType.Likes));
        ret.add(ZodiacSignManager.getInstance().getZodiacSignQuizPairs(QuizType.Dislikes));
        
        return ret;
    }
}
