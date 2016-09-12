package org.wilson.world.personality;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.PersonalityManager;
import org.wilson.world.quiz.MultipleContainsQuizResultBuilder;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizBuilderStrategy;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.SystemQuiz;

public class PersonalityQuiz extends SystemQuiz {
    public PersonalityQuiz() {
        this.setName("Personality Quiz");
        this.setDescription("A quiz for testing the personalities");
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
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of doing personality quiz");
        }
        
        Event event = new Event();
        event.type = EventType.DoPersonalityQuiz;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("personality.quiz.max_length", 5);
        List<PersonalityQuizPair> pairs = PersonalityManager.getInstance().getPersonalityQuizPairs();
        
        QuizBuilder builder = new QuizBuilder(){

            @Override
            public int getId(Object target) {
                return ((PersonalityQuizPair)target).id;
            }

            @Override
            public String getTop(Object target) {
                return ((PersonalityQuizPair)target).top;
            }

            @Override
            public String getBottom(Object target) {
                return ((PersonalityQuizPair)target).bottom;
            }
            
        }.setQuizItemMode(QuizItemMode.Multiple).setSize(maxLength).setStrategy(QuizBuilderStrategy.ShowTop).setTargets(pairs);
        
        this.setItems(builder.build());
    }
}
