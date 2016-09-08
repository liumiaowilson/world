package org.wilson.world.zodiac_sign;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.ZodiacSignManager;
import org.wilson.world.quiz.MultipleContainsQuizResultBuilder;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizBuilderStrategy;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.SystemQuiz;

public class ZodiacSignQuiz extends SystemQuiz {
    private QuizType type;
    
    public ZodiacSignQuiz() {
        this.setName("Zodiac Sign Quiz");
        this.setDescription("A quiz for testing the zodiac signs");
    }

    public QuizType getType() {
        return type;
    }

    public void setType(QuizType type) {
        this.type = type;
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
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of doing zodiac sign quiz");
        }
        
        Event event = new Event();
        if(QuizType.Date == type) {
            event.type = EventType.DoZSDateQuiz;
        }
        else if(QuizType.Strengths == type) {
            event.type = EventType.DoZSStrengthsQuiz;
        }
        else if(QuizType.Weaknesses == type) {
            event.type = EventType.DoZSWeaknessesQuiz;
        }
        else if(QuizType.Likes == type) {
            event.type = EventType.DoZSLikesQuiz;
        }
        else if(QuizType.Dislikes == type) {
            event.type = EventType.DoZSDislikesQuiz;
        }
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("zodiac_sign.quiz.max_length", 5);
        List<ZodiacSignQuizPair> pairs = ZodiacSignManager.getInstance().getZodiacSignQuizPairs(this.type);
        
        QuizBuilder builder = new QuizBuilder(){

            @Override
            public int getId(Object target) {
                return ((ZodiacSignQuizPair)target).id;
            }

            @Override
            public String getTop(Object target) {
                return ((ZodiacSignQuizPair)target).top;
            }

            @Override
            public String getBottom(Object target) {
                return ((ZodiacSignQuizPair)target).bottom;
            }
            
        }.setQuizItemMode(QuizItemMode.Multiple).setSize(maxLength).setStrategy(QuizBuilderStrategy.ShowTop).setTargets(pairs);
        
        this.setItems(builder.build());
    }
}
