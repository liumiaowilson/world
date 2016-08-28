package org.wilson.world.rhetoric;

import java.util.List;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.RhetoricManager;
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

public class RhetoricQuiz extends SystemQuiz {
    public RhetoricQuiz() {
        this.setName("Rhetoric Quiz");
        this.setDescription("A quiz for testing the rhetorical devices");
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
