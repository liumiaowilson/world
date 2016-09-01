package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.WeaselPhrase;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class WeaselPhraseManager {
    public static final String NAME = "weasel_phrase";
    
    private static WeaselPhraseManager instance;
    
    private DAO<WeaselPhrase> dao = null;
    
    @SuppressWarnings("unchecked")
    private WeaselPhraseManager() {
        this.dao = DAOManager.getInstance().getDAO(WeaselPhrase.class);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(WeaselPhrase phrase : getWeaselPhrases()) {
                    boolean found = phrase.pattern.contains(text) || phrase.usage.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = phrase.id;
                        content.name = phrase.pattern;
                        content.description = phrase.usage;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static WeaselPhraseManager getInstance() {
        if(instance == null) {
            instance = new WeaselPhraseManager();
        }
        return instance;
    }
    
    public void createWeaselPhrase(WeaselPhrase phrase) {
    }
    
    public WeaselPhrase getWeaselPhrase(int id) {
        WeaselPhrase phrase = this.dao.get(id);
        if(phrase != null) {
            return phrase;
        }
        else {
            return null;
        }
    }
    
    public List<WeaselPhrase> getWeaselPhrases() {
        List<WeaselPhrase> result = new ArrayList<WeaselPhrase>();
        for(WeaselPhrase phrase : this.dao.getAll()) {
            result.add(phrase);
        }
        return result;
    }
    
    public void updateWeaselPhrase(WeaselPhrase phrase) {
    }
    
    public void deleteWeaselPhrase(int id) {
    }

    public WeaselPhrase randomWeaselPhrase() {
        List<WeaselPhrase> phrases = this.getWeaselPhrases();
        if(phrases.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(phrases.size());
        return phrases.get(n);
    }
}
