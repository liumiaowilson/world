package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.quiz.QuizItem;

public class QuizTest {

    @Test
    public void testQuizData() {
        List<QuizItem> ret = null;
        InputStream in = null;
        try {
            String file = "personality_compass_quiz.json";
            in = this.getClass().getClassLoader().getResourceAsStream(file);
            String json = IOUtils.toString(in);
            ret = QuizDataManager.toQuizItems(json);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if(ret != null) {
            System.out.println(ret);
        }
    }

}
