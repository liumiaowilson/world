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
import org.wilson.world.model.Sign;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.TimeUtils;

public class SignManager implements ItemTypeProvider, ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(SignManager.class);
    
    public static final String FLASHCARD_SET_NAME = "Signs";
    
    public static final String NAME = "sign";
    
    private static SignManager instance;
    
    private DAO<Sign> dao = null;
    
    private Cache<String, String> meanings = null;
    
    private FlashCardSet set = null;
    
    private Map<Integer, Long> steps = new HashMap<Integer, Long>();
    
    @SuppressWarnings("unchecked")
    private SignManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Sign.class);
        this.meanings = new DefaultCache<String, String>("sign_manager_meanings", false);
        
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
                
                for(Sign sign : getSigns()) {
                    boolean found = sign.name.contains(text) || sign.meaning.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = sign.id;
                        content.name = sign.name;
                        content.description = sign.meaning;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static SignManager getInstance() {
        if(instance == null) {
            instance = new SignManager();
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
    
    public void createSign(Sign sign) {
        ItemManager.getInstance().checkDuplicate(sign);
        
        FlashCard card = new FlashCard();
        card.setId = this.set.id;
        card.name = sign.name;
        if(card.name.length() > 20) {
            card.name = card.name.substring(0, 20);
        }
        card.top = card.name;
        card.bottom = sign.meaning;
        if(card.bottom.length() > 400) {
            card.bottom = card.bottom.substring(0, 400);
        }
        FlashCardManager.getInstance().createFlashCard(card);
        
        sign.cardId = card.id;
        sign.time = System.currentTimeMillis();
        this.dao.create(sign);
    }
    
    public Sign getSign(int id) {
        Sign sign = this.dao.get(id);
        if(sign != null) {
            this.loadSign(sign);
            return sign;
        }
        else {
            return null;
        }
    }
    
    private void loadSign(Sign sign) {
        if(sign != null) {
            FlashCard card = FlashCardManager.getInstance().getFlashCard(sign.cardId);
            if(card != null) {
                sign.name = card.top;
                sign.meaning = card.bottom;
            }
        }
    }
    
    public List<Sign> getSigns() {
        List<Sign> result = new ArrayList<Sign>();
        for(Sign sign : this.dao.getAll()) {
            this.loadSign(sign);
            result.add(sign);
        }
        return result;
    }
    
    public void updateSign(Sign sign) {
        FlashCard card = FlashCardManager.getInstance().getFlashCard(sign.cardId);
        if(!sign.name.equals(card.top) || !sign.meaning.equals(card.bottom)) {
            card.name = sign.name;
            if(card.name.length() > 20) {
                card.name = card.name.substring(0, 20);
            }
            card.top = card.name;
            card.bottom = sign.meaning;
            if(card.bottom.length() > 400) {
                card.bottom = card.bottom.substring(0, 400);
            }
            
            FlashCardManager.getInstance().updateFlashCard(card);
        }
        
        sign.time = System.currentTimeMillis();
        this.dao.update(sign);
    }
    
    public void deleteSign(int id) {
        Sign sign = this.getSign(id);
        
        FlashCard card = FlashCardManager.getInstance().getFlashCard(sign.cardId);
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
        return target instanceof Sign;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Sign sign = (Sign)target;
        return String.valueOf(sign.id);
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
        
        Sign sign = (Sign)target;
        return sign.name;
    }

    @Override
    public void start() {
        //ensure flashcard set exists
        this.set = FlashCardSetManager.getInstance().getFlashCardSet(FLASHCARD_SET_NAME);
        if(this.set == null) {
            logger.error("Flash card set [" + FLASHCARD_SET_NAME + "] could not be found.");
        }
        
        Cache<Integer, Sign> cache = ((CachedDAO<Sign>)this.dao).getCache();
        cache.addCacheListener(new CacheListener<Sign>(){

            @Override
            public void cachePut(Sign old, Sign v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                SignManager.this.meanings.put(v.name, v.meaning);
            }

            @Override
            public void cacheDeleted(Sign v) {
                SignManager.this.meanings.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<Sign> all) {
            }

            @Override
            public void cacheLoading(List<Sign> old) {
                SignManager.this.meanings.clear();
            }
            
        });
        cache.notifyLoaded();
    }

    @Override
    public void shutdown() {
    }
    
    public String getMeaning(String sign) {
        return this.meanings.get(sign);
    }
    
    public int getMaxStep() {
        return 8;
    }
    
    public long getStepDuration(int step) {
        return this.steps.get(steps);
    }
    
    public List<Sign> getForgettingSigns() {
        List<Sign> ret = new ArrayList<Sign>();
        
        long now = System.currentTimeMillis();
        for(Sign sign : this.getSigns()) {
            long duration = this.steps.get(sign.step);
            if(now >= sign.time + duration) {
                ret.add(sign);
            }
        }
        
        return ret;
    }
}
