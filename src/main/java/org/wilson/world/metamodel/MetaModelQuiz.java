package org.wilson.world.metamodel;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.MetaModelManager;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizBuilder;
import org.wilson.world.quiz.QuizBuilderStrategy;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.ScoreQuizProcessor;
import org.wilson.world.quiz.SystemQuiz;

public class MetaModelQuiz extends SystemQuiz {
    public MetaModelQuiz() {
        this.setName("Meta Model Quiz");
        this.setDescription("A quiz for testing the meta models");
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        Quiz quiz = paper.getQuiz();
        List<QuizItem> items = quiz.getQuizItems();
        
        int total = items.size();
        int sum = 0;
        StringBuffer sb = new StringBuffer();
        for(QuizItem item : items) {
            List<Integer> selection = paper.getSelections(item.id);
            if(selection == null || selection.isEmpty()) {
                continue;
            }
            List<Integer> expected = ScoreQuizProcessor.getNonZeroValueQuizItemOptions(item);
            if(expected.isEmpty()) {
                continue;
            }
            int opt = expected.get(0);
            boolean wrong = !selection.contains(opt);
            
            if(!wrong) {
                sum += 1;
            }
            else {
                QuizItemOption option = item.getOptionById(opt);
                sb.append("<p><b>" + item.question + "</b></p>");
                sb.append("<p><i>" + option.answer + "</i></p>");
            }
        }
        
        QuizResult result = new QuizResult();
        result.data.put("total", total);
        result.data.put("sum", sum);
        result.message = "Scored [" + sum + "] out of [" + total + "]<hr/>" + sb.toString();
        
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
        List<MetaModelQuizPair> pairs = MetaModelManager.getInstance().getMetaModelQuizPairs();
        
        QuizBuilder builder = new QuizBuilder(){

            @Override
            public int getId(Object target) {
                return ((MetaModelQuizPair)target).id;
            }

            @Override
            public String getTop(Object target) {
                return ((MetaModelQuizPair)target).top;
            }

            @Override
            public String getBottom(Object target) {
                return ((MetaModelQuizPair)target).bottom;
            }
            
        }.setQuizItemMode(QuizItemMode.Multiple).setSize(maxLength).setStrategy(QuizBuilderStrategy.ShowTop).setTargets(pairs);
        
        this.setItems(builder.build());
    }
}
