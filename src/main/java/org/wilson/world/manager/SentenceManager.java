package org.wilson.world.manager;

import org.wilson.world.sentence.RandomSentenceJob;
import org.wilson.world.sentence.SentenceInfo;
import org.wilson.world.web.WebJob;

public class SentenceManager {
    private static SentenceManager instance;
    
    private SentenceManager() {
        
    }
    
    public static SentenceManager getInstance() {
        if(instance == null) {
            instance = new SentenceManager();
        }
        
        return instance;
    }
    
    public SentenceInfo randomSentence() {
        WebJob job = WebManager.getInstance().getAvailableWebJobByName(RandomSentenceJob.class.getSimpleName());
        if(job == null) {
            return null;
        }
        
        WebManager.getInstance().run(job);
        
        SentenceInfo info = (SentenceInfo) WebManager.getInstance().get(RandomSentenceJob.SENTENCE);
        return info;
    }
}
