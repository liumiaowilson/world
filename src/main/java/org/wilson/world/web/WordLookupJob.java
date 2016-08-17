package org.wilson.world.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.WebManager;

public class WordLookupJob extends SystemWebJob {
    public static final String WORD_LOOKUP = "word_lookup";
    public static final String WORD = "word";

    public WordLookupJob() {
        this.setDescription("Look up the meaning of a word");
    }
    
    @Override
    public void run() throws Exception {
        String word = (String) WebManager.getInstance().get(WORD);
        if(StringUtils.isBlank(word)) {
            return;
        }
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("par1", word);
        String result = WebManager.getInstance().doPost("http://www.easydefine.com/modules/test.inc.php", data);
        if(!StringUtils.isBlank(result)) {
            WordInfo info = new WordInfo();
            info.name = word;
            info.explanation = result;
            WebManager.getInstance().put(WORD_LOOKUP, info);
        }
    }

}
