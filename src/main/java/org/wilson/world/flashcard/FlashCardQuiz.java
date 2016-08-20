package org.wilson.world.flashcard;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.FlashCardManager;
import org.wilson.world.model.FlashCard;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.SystemQuiz;

public class FlashCardQuiz extends SystemQuiz {
    private int setId;
    
    public FlashCardQuiz() {
        this.setName("Flash Card Quiz");
        this.setDescription("Quiz based on the flash cards");
    }
    
    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    @Override
    public void init() {
        super.init();
        
        int length = ConfigManager.getInstance().getConfigAsInt("flashcard.quiz.length", 5);
        List<QuizItem> items = new ArrayList<QuizItem>();
        
        for(int i = 0; i < length; i++) {
            QuizItem item = this.genQuizItem(i);
            if(item != null) {
                items.add(item);
            }
        }
        
        this.setItems(items);
    }

    private QuizItem genQuizItem(int id) {
        QuizItem item = new QuizItem();
        item.id = id;
        item.mode = QuizItemMode.Single;
        
        List<FlashCard> cards = FlashCardManager.getInstance().getFlashCardsBySet(setId);
        if(cards == null || cards.isEmpty()) {
            return null;
        }
        
        List<FlashCard> selected = this.random(cards);
        int n = DiceManager.getInstance().random(selected.size());
        FlashCard key = selected.get(n);
        if(DiceManager.getInstance().dice(50)) {
            //top
            item.question = key.top;
            for(int i = 0; i < selected.size(); i++) {
                FlashCard optionCard = selected.get(i);
                QuizItemOption option = new QuizItemOption();
                option.id = i + 1;
                option.name = optionCard.bottom;
                option.answer = option.name;
                option.value = option.answer.equals(key.bottom) ? 1 : 0;
                item.options.add(option);
            }
        }
        else {
            //bottom
            item.question = key.bottom;
            for(int i = 0; i < selected.size(); i++) {
                FlashCard optionCard = selected.get(i);
                QuizItemOption option = new QuizItemOption();
                option.id = i + 1;
                option.name = optionCard.top;
                option.answer = option.name;
                option.value = option.answer.equals(key.top) ? 1 : 0;
                item.options.add(option);
            }
        }
        
        return item;
    }
    
    private List<FlashCard> random(List<FlashCard> cards) {
        if(cards.size() <= 4) {
            return cards;
        }
        
        return DiceManager.getInstance().random(cards, 4);
    }
}
