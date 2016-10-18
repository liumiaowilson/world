package org.wilson.world.sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SignManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.model.Sign;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizProcessor;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.ScoreQuizProcessor;
import org.wilson.world.quiz.SystemQuiz;

public class SignQuiz extends SystemQuiz {
    public SignQuiz() {
        this.setName("Sign Quiz");
        this.setDescription("A quiz for testing the signs you just remembered");
    }

    @Override
    public QuizResult doProcess(QuizPaper paper) {
        QuizProcessor processor = this.getProcessor();
        if(processor == null) {
            processor = new ScoreQuizProcessor();
        }
        
        Quiz quiz = paper.getQuiz();
        final List<Sign> updates = new ArrayList<Sign>();
        List<QuizItem> items = quiz.getQuizItems();
        for(QuizItem item : items) {
            int selection = paper.getSelection(item.id);
            QuizItemOption option = item.getOptionById(selection);
            if(option.value == 1) {
                Sign sign = SignManager.getInstance().getSign(item.id);
                if(sign != null) {
                    updates.add(sign);
                }
            }
        }
        
        double ratio = updates.size() * 1.0 / items.size();
        if(ratio >= 0.6) {
            int exp = ExpManager.getInstance().getExp();
            exp += 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of training sign");
        }
        
        ThreadPoolManager.getInstance().execute(new Runnable(){

            @Override
            public void run() {
                for(Sign sign : updates) {
                    sign.step += 1;
                    if(sign.step > SignManager.getInstance().getMaxStep()) {
                        SignManager.getInstance().deleteSign(sign.id);
                    }
                    else {
                        SignManager.getInstance().updateSign(sign);
                    }
                }
            }
            
        });
        
        QuizResult result = processor.process(paper);
        
        Event event = new Event();
        event.type = EventType.TrainSign;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("sign.quiz.max_length", 5);
        List<Sign> signs = SignManager.getInstance().getForgettingSigns();
        Collections.sort(signs, new Comparator<Sign>(){

            @Override
            public int compare(Sign o1, Sign o2) {
                return o1.step - o2.step;
            }
            
        });
        int length = maxLength;
        if(length > signs.size()) {
            length = signs.size();
        }
        
        List<QuizItem> items = new ArrayList<QuizItem>();
        
        for(int i = 0; i < length; i++) {
            Sign sign = signs.get(i);
            QuizItem item = this.genQuizItem(sign);
            if(item != null) {
                items.add(item);
            }
        }
        
        this.setItems(items);
    }
    
    private QuizItem genQuizItem(Sign sign) {
        QuizItem item = new QuizItem();
        item.id = sign.id;
        item.mode = QuizItemMode.Single;
        item.name = sign.name;
        item.question = item.name;
        
        List<Sign> all = SignManager.getInstance().getSigns();
        all.remove(sign);
        List<Sign> candidates = null;
        if(all.size() < 3) {
            candidates = all;
        }
        else {
            candidates = DiceManager.getInstance().random(all, 3);
        }
        
        QuizItemOption option = new QuizItemOption();
        option.id = 1;
        option.name = sign.meaning;
        option.answer = option.name;
        option.value = 1;
        item.options.add(option);
        
        for(int i = 0; i < candidates.size(); i++) {
            Sign candidate = candidates.get(i);
            option = new QuizItemOption();
            option.id = 2 + i;
            option.name = candidate.meaning;
            option.answer = option.name;
            option.value = 0;
            item.options.add(option);
        }
        
        Collections.shuffle(item.options);
        
        return item;
    }

    @Override
    public boolean isPublic() {
        return true;
    }
}
