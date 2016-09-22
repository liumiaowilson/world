package org.wilson.world.quiz;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MultipleContainsQuizResultBuilder {
    private QuizPaper paper;
    
    public MultipleContainsQuizResultBuilder(QuizPaper paper) {
        this.paper = paper;
    }
    
    public QuizResult build() {
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
                if(!StringUtils.isBlank(option.url)) {
                    sb.append("<p><i><a href=\"" + option.url + "\">" + option.answer + "</a></i></p>");
                }
                else {
                    sb.append("<p><i>" + option.answer + "</i></p>");
                }
            }
        }
        
        QuizResult result = new QuizResult();
        result.data.put("total", total);
        result.data.put("sum", sum);
        result.message = "Scored [" + sum + "] out of [" + total + "]<hr/>" + sb.toString();
        
        return result;
    }
}
