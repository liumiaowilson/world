package org.wilson.world.quiz;

import org.wilson.world.ext.Scriptable;

public interface QuizProcessor {
    public static final String EXTENSION_POINT = "quiz.processor";
    
    @Scriptable(name = EXTENSION_POINT, description = "Process the quiz result. Assign the value of this action to quiz data.", params = { "paper" })
    public QuizResult process(QuizPaper paper);
}
