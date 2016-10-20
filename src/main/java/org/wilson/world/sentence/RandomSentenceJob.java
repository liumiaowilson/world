package org.wilson.world.sentence;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class RandomSentenceJob extends SystemWebJob {
    public static final String SENTENCE = "sentence";

    public RandomSentenceJob() {
        this.setDescription("Get a random sentence");
    }
    
    @Override
    public void run() throws Exception {
        String content = WebManager.getInstance().getContent("http://josephrocca.com/randomsentence/processrequest.php");
        if(!StringUtils.isBlank(content)) {
            String [] items = content.split("\\|");
            if(items.length >= 2) {
                SentenceInfo info = new SentenceInfo();
                info.sentence = items[0].trim();
                info.from = items[1].trim();
                WebManager.getInstance().put(SENTENCE, info);
            }
        }
    }

}
