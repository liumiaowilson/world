package org.wilson.world.web.word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.manager.WordManager;
import org.wilson.world.model.Word;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizProcessor;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.quiz.ScoreQuizProcessor;
import org.wilson.world.quiz.SystemQuiz;

public class WordQuiz extends SystemQuiz {
    public WordQuiz() {
        this.setName("Word Quiz");
        this.setDescription("A quiz for testing the words you just remembered");
    }

    @Override
    public QuizResult process(QuizPaper paper) {
        QuizProcessor processor = this.getProcessor();
        if(processor == null) {
            processor = new ScoreQuizProcessor();
        }
        
        Quiz quiz = paper.getQuiz();
        final List<Word> updates = new ArrayList<Word>();
        List<QuizItem> items = quiz.getQuizItems();
        for(QuizItem item : items) {
            int selection = paper.getSelection(item.id);
            QuizItemOption option = item.getOptionById(selection);
            if(option.value == 1) {
                Word word = WordManager.getInstance().getWord(item.id);
                if(word != null) {
                    updates.add(word);
                }
            }
        }
        
        double ratio = updates.size() * 1.0 / items.size();
        if(ratio >= 0.6) {
            int exp = ExpManager.getInstance().getExp();
            exp += 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience because of training word");
        }
        
        ThreadPoolManager.getInstance().execute(new Runnable(){

            @Override
            public void run() {
                for(Word word : updates) {
                    word.step += 1;
                    if(word.step > WordManager.getInstance().getMaxStep()) {
                        WordManager.getInstance().deleteWord(word.id);
                    }
                    else {
                        WordManager.getInstance().updateWord(word);
                    }
                }
            }
            
        });
        
        QuizResult result = processor.process(paper);
        
        Event event = new Event();
        event.type = EventType.TrainWord;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }

    @Override
    public void init() {
        super.init();
        
        int maxLength = ConfigManager.getInstance().getConfigAsInt("word.quiz.max_length", 5);
        List<Word> words = WordManager.getInstance().getForgettingWords();
        int length = maxLength;
        if(length > words.size()) {
            length = words.size();
        }
        
        List<QuizItem> items = new ArrayList<QuizItem>();
        
        for(int i = 0; i < length; i++) {
            Word word = words.get(i);
            QuizItem item = this.genQuizItem(word);
            if(item != null) {
                items.add(item);
            }
        }
        
        this.setItems(items);
    }
    
    private QuizItem genQuizItem(Word word) {
        QuizItem item = new QuizItem();
        item.id = word.id;
        item.mode = QuizItemMode.Single;
        item.name = word.name;
        item.question = item.name;
        
        List<Word> all = WordManager.getInstance().getWords();
        all.remove(word);
        List<Word> candidates = null;
        if(all.size() < 3) {
            candidates = all;
        }
        else {
            candidates = DiceManager.getInstance().random(all, 3);
        }
        
        QuizItemOption option = new QuizItemOption();
        option.id = 1;
        option.name = word.meaning;
        option.answer = option.name;
        option.value = 1;
        item.options.add(option);
        
        for(int i = 0; i < candidates.size(); i++) {
            Word candidate = candidates.get(i);
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
}
