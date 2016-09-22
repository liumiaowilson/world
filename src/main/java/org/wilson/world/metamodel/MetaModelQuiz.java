package org.wilson.world.metamodel;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.MetaModelManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.quiz.MultipleContainsQuizResultBuilder;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuizBuilder;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.SystemQuiz;

public class MetaModelQuiz extends SystemQuiz {
    public MetaModelQuiz() {
        this.setName("Meta Model Quiz");
        this.setDescription("A quiz for testing the meta models");
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
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of doing meta model quiz");
        }
        
        Event event = new Event();
        event.type = EventType.DoMetaModelQuiz;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("meta_model.quiz.max_length", 5);
        List<QuizPair> pairs = MetaModelManager.getInstance().getMetaModelQuizPairs();
        
        QuizBuilder builder = new QuizPairQuizBuilder().setSize(maxLength).setTargets(pairs);
        
        this.setItems(builder.build());
    }
}
