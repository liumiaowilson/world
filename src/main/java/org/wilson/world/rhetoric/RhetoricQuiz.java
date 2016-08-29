package org.wilson.world.rhetoric;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.RhetoricManager;
import org.wilson.world.quiz.MultipleContainsQuizResultBuilder;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizBuilderStrategy;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.SystemQuiz;

public class RhetoricQuiz extends SystemQuiz {
    public RhetoricQuiz() {
        this.setName("Rhetoric Quiz");
        this.setDescription("A quiz for testing the rhetorical devices");
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
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of doing rhetoric quiz");
        }
        
        Event event = new Event();
        event.type = EventType.DoRhetoricQuiz;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("rhetoric.quiz.max_length", 5);
        List<RhetoricQuizPair> pairs = RhetoricManager.getInstance().getRhetoricQuizPairs();
        
        QuizBuilder builder = new QuizBuilder(){

            @Override
            public int getId(Object target) {
                return ((RhetoricQuizPair)target).id;
            }

            @Override
            public String getTop(Object target) {
                return ((RhetoricQuizPair)target).top;
            }

            @Override
            public String getBottom(Object target) {
                return ((RhetoricQuizPair)target).bottom;
            }
            
        }.setQuizItemMode(QuizItemMode.Multiple).setSize(maxLength).setStrategy(QuizBuilderStrategy.ShowTop).setTargets(pairs);
        
        this.setItems(builder.build());
    }
}
