package org.wilson.world.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        Document doc = WebManager.getInstance().parse("http://www.merriam-webster.com/dictionary/" + word);
        Elements elements = doc.select("div.quick-def-box");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            
            String part = element.select("span.main-attr").text();
            String pronunciation = element.select("span.pr").text();
            String definition = element.select("div.def-text p.definition-inner-item").text();
            
            WordInfo info = new WordInfo();
            info.name = word;
            info.type = part;
            info.pronunciation = pronunciation;
            List<String> explanations = new ArrayList<String>();
            for(String item : definition.split(":")) {
                if(!StringUtils.isBlank(item.trim())) {
                    explanations.add(item.trim());
                }
            }
            info.explanations = explanations.toArray(new String[0]);
            WebManager.getInstance().put(WORD_LOOKUP, info);
        }
    }

}
