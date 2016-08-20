package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.FlashCard;
import org.wilson.world.model.FlashCardSet;
import org.wilson.world.model.Word;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.WordInfo;

public class WordManager implements ItemTypeProvider, ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(WordManager.class);
    
    public static final String FLASHCARD_SET_NAME = "Words";
    
    public static final String NAME = "word";
    
    private static WordManager instance;
    
    private DAO<Word> dao = null;
    
    private Cache<String, String> meanings = null;
    
    private FlashCardSet set = null;
    
    private Map<Integer, Long> steps = new HashMap<Integer, Long>();
    
    @SuppressWarnings("unchecked")
    private WordManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Word.class);
        this.meanings = new DefaultCache<String, String>("word_manager_meanings", false);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        this.initSteps();
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Word word : getWords()) {
                    boolean found = word.name.contains(text) || word.meaning.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = word.id;
                        content.name = word.name;
                        content.description = word.meaning;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static WordManager getInstance() {
        if(instance == null) {
            instance = new WordManager();
        }
        return instance;
    }
    
    private void initSteps() {
        this.steps.put(1, 5 * TimeUtils.MINUTE_DURATION);
        this.steps.put(2, 30 * TimeUtils.MINUTE_DURATION);
        this.steps.put(3, 12 * TimeUtils.HOUR_DURATION);
        this.steps.put(4, 1 * TimeUtils.DAY_DURATION);
        this.steps.put(5, 2 * TimeUtils.DAY_DURATION);
        this.steps.put(6, 4 * TimeUtils.DAY_DURATION);
        this.steps.put(7, 7 * TimeUtils.DAY_DURATION);
        this.steps.put(8, 15 * TimeUtils.DAY_DURATION);
    }
    
    public void createWord(Word word) {
        ItemManager.getInstance().checkDuplicate(word);
        
        FlashCard card = new FlashCard();
        card.setId = this.set.id;
        card.name = word.name;
        if(card.name.length() > 20) {
            card.name = card.name.substring(0, 20);
        }
        card.top = card.name;
        card.bottom = word.meaning;
        if(card.bottom.length() > 400) {
            card.bottom = card.bottom.substring(0, 400);
        }
        FlashCardManager.getInstance().createFlashCard(card);
        
        word.cardId = card.id;
        word.time = System.currentTimeMillis();
        this.dao.create(word);
    }
    
    public Word getWord(int id) {
        Word word = this.dao.get(id);
        if(word != null) {
            this.loadWord(word);
            return word;
        }
        else {
            return null;
        }
    }
    
    private void loadWord(Word word) {
        if(word != null) {
            FlashCard card = FlashCardManager.getInstance().getFlashCard(word.cardId);
            if(card != null) {
                word.name = card.top;
                word.meaning = card.bottom;
            }
        }
    }
    
    public List<Word> getWords() {
        List<Word> result = new ArrayList<Word>();
        for(Word word : this.dao.getAll()) {
            this.loadWord(word);
            result.add(word);
        }
        return result;
    }
    
    public void updateWord(Word word) {
        FlashCard card = FlashCardManager.getInstance().getFlashCard(word.cardId);
        if(!word.name.equals(card.top) || !word.meaning.equals(card.bottom)) {
            card.name = word.name;
            if(card.name.length() > 20) {
                card.name = card.name.substring(0, 20);
            }
            card.top = card.name;
            card.bottom = word.meaning;
            if(card.bottom.length() > 400) {
                card.bottom = card.bottom.substring(0, 400);
            }
            
            FlashCardManager.getInstance().updateFlashCard(card);
        }
        
        word.time = System.currentTimeMillis();
        this.dao.update(word);
    }
    
    public void deleteWord(int id) {
        Word word = this.getWord(id);
        
        FlashCard card = FlashCardManager.getInstance().getFlashCard(word.cardId);
        if(card != null) {
            FlashCardManager.getInstance().deleteFlashCard(card.id);
        }
        
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Word;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Word word = (Word)target;
        return String.valueOf(word.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Word idea = (Word)target;
        return idea.name;
    }

    @Override
    public void start() {
        //ensure flashcard set exists
        this.set = FlashCardSetManager.getInstance().getFlashCardSet(FLASHCARD_SET_NAME);
        if(this.set == null) {
            logger.error("Flash card set [" + FLASHCARD_SET_NAME + "] could not be found.");
        }
        
        Cache<Integer, Word> cache = ((CachedDAO<Word>)this.dao).getCache();
        cache.addCacheListener(new CacheListener<Word>(){

            @Override
            public void cachePut(Word old, Word v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                WordManager.this.meanings.put(v.name, v.meaning);
            }

            @Override
            public void cacheDeleted(Word v) {
                WordManager.this.meanings.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<Word> all) {
            }

            @Override
            public void cacheLoading(List<Word> old) {
                WordManager.this.meanings.clear();
            }
            
        });
        cache.notifyLoaded();
    }

    @Override
    public void shutdown() {
    }
    
    public String getMeaning(String word) {
        return this.meanings.get(word);
    }
    
    public void createWord(WordInfo info) {
        if(info == null) {
            return;
        }
        
        Word word = new Word();
        word.name = info.name;
        word.meaning = info.explanation;
        word.meaning = FormatUtils.htmlToText(word.meaning);
        word.step = 1;
        
        this.createWord(word);
    }
    
    public WordInfo toWordInfo(Word word) {
        if(word == null) {
            return null;
        }
        
        WordInfo ret = new WordInfo();
        
        ret.name = word.name;
        ret.explanation = word.meaning;
        
        return ret;
    }
    
    public int getMaxStep() {
        return 8;
    }
    
    public long getStepDuration(int step) {
        return this.steps.get(steps);
    }
    
    public List<Word> getForgettingWords() {
        List<Word> ret = new ArrayList<Word>();
        
        long now = System.currentTimeMillis();
        for(Word word : this.getWords()) {
            long duration = this.steps.get(word.step);
            if(now >= word.time + duration) {
                ret.add(word);
            }
        }
        
        return ret;
    }
}
