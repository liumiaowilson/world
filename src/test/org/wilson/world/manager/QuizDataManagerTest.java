package org.wilson.world.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;

public class QuizDataManagerTest {

    @Test
    public void testFromJSON() throws IOException {
        List<QuizItem> items = QuizDataManager.toQuizItems(QuizDataManager.getSampleContent());
        System.out.println(items);
    }

    @Test
    public void testToJSON() {
        List<QuizItem> items = new ArrayList<QuizItem>();
        QuizItem item = new QuizItem();
        item.id = 1;
        item.name = "Name";
        item.mode = QuizItemMode.Single;
        item.question = "What is your name?";
        
        QuizItemOption option1 = new QuizItemOption();
        option1.id = 1;
        option1.name = "Wilson";
        option1.answer = "Wilson";
        option1.value = 1;
        item.options.add(option1);
        
        QuizItemOption option2 = new QuizItemOption();
        option2.id = 2;
        option2.name = "Coco";
        option2.answer = "Coco";
        option2.value = 0;
        item.options.add(option2);
        
        items.add(item);
        
        String ret = QuizDataManager.fromQuizItems(items);
        System.out.println(ret);
    }
}
