package org.wilson.world.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreQuizProcessor implements QuizProcessor {

    @Override
    public QuizResult process(QuizPaper paper) {
        Quiz quiz = paper.getQuiz();
        List<QuizItem> items = quiz.getQuizItems();
        
        int total = items.size();
        int sum = 0;
        for(QuizItem item : items) {
            List<Integer> selection = paper.getSelections(item.id);
            if(selection == null || selection.isEmpty()) {
                continue;
            }
            if(QuizItemMode.Single.equals(item.mode)) {
                if(selection.size() != 1) {
                    continue;
                }
                int selectionId = selection.get(0);
                QuizItemOption option = item.getOptionById(selectionId);
                if(option != null && option.value != 0) {
                    sum += 1;
                }
            }
            else if(QuizItemMode.Multiple.equals(item.mode)) {
                List<Integer> expected = getNonZeroValueQuizItemOptions(item);
                if(expected.size() != selection.size()) {
                    continue;
                }
                boolean wrong = false;
                for(Integer i : selection) {
                    if(!expected.contains(i)) {
                        wrong = true;
                        break;
                    }
                }
                
                if(!wrong) {
                    sum += 1;
                }
            }
        }
        
        QuizResult result = new QuizResult();
        result.data.put("total", total);
        result.data.put("sum", sum);
        result.message = "Scored [" + sum + "] out of [" + total + "]";
        
        return result;
    }

    public static List<Integer> getNonZeroValueQuizItemOptions(QuizItem item) {
        if(item == null) {
            return Collections.emptyList();
        }
        
        List<Integer> ret = new ArrayList<Integer>();
        
        for(QuizItemOption option : item.options) {
            if(option.value != 0) {
                ret.add(option.id);
            }
        }
        
        return ret;
    }
}
