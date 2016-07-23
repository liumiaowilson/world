package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Quest;
import org.wilson.world.model.QuestDef;
import org.wilson.world.model.QuestInfo;
import org.wilson.world.quest.QuestAchieveEventListener;

public class QuestManager implements ItemTypeProvider {
    public static final String NAME = "quest";
    
    private static QuestManager instance;
    
    private DAO<Quest> dao = null;
    
    private Cache<String, QuestInfo> cache = null;
    
    @SuppressWarnings("unchecked")
    private QuestManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Quest.class);
        this.cache = new DefaultCache<String, QuestInfo>("quest_manager_cache", false);
        ((CachedDAO<Quest>)this.dao).getCache().addCacheListener(new CacheListener<Quest>(){

            @Override
            public void cachePut(Quest old, Quest v) {
                //do not support quest def change
                QuestDef def = QuestDefManager.getInstance().getQuestDef(v.defId);
                QuestInfo info = getCache().get(def.name);
                if(info == null) {
                    info = new QuestInfo();
                    info.name = def.name;
                    info.def = def;
                    info.count = 1;
                    getCache().put(info.name, info);
                }
                else {
                    info.count = info.count + 1;
                }
                adjustPays();
            }

            @Override
            public void cacheDeleted(Quest v) {
                QuestDef def = QuestDefManager.getInstance().getQuestDef(v.defId);
                QuestInfo info = QuestManager.this.cache.get(def.name);
                if(info != null) {
                    if(info.count > 1) {
                        info.count = info.count - 1;
                    }
                    else {
                        getCache().delete(def.name);
                    }
                    adjustPays();
                }
            }

            @Override
            public void cacheLoaded(List<Quest> all) {
            }

            @Override
            public void cacheLoading(List<Quest> old) {
                getCache().clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        EventManager.getInstance().registerListener(EventType.CreateQuest, new QuestAchieveEventListener());
    }
    
    public static QuestManager getInstance() {
        if(instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }
    
    private Cache<String, QuestInfo> getCache() {
        return this.cache;
    }
    
    public void adjustPays() {
        for(QuestInfo info : this.cache.getAll()) {
            adjustPay(info);
        }
    }
    
    public void adjustPay(QuestInfo info) {
        if(info == null) {
            return;
        }
        if(info.def == null) {
            return;
        }
        int total = this.dao.getAll().size();
        if(total == 0) {
            return;
        }
        int num = info.count;
        int pay = info.def.pay;
        double factor = 1.0 - num * 1.0 / total;
        if(factor < 0.25) {
            factor = 0.25;
        }
        pay = (int) (pay * factor);
        if(pay < 1) {
            pay = 1;
        }
        info.pay = pay;
    }
    
    public void createQuest(Quest quest) {
        this.dao.create(quest);
    }
    
    public Quest getQuest(int id) {
        Quest quest = this.dao.get(id);
        if(quest != null) {
            return quest;
        }
        else {
            return null;
        }
    }
    
    public List<Quest> getQuests() {
        List<Quest> result = new ArrayList<Quest>();
        for(Quest quest : this.dao.getAll()) {
            result.add(quest);
        }
        return result;
    }
    
    public void updateQuest(Quest quest) {
        this.dao.update(quest);
    }
    
    public void deleteQuest(int id) {
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
        return target instanceof Quest;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Quest quest = (Quest)target;
        return String.valueOf(quest.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<QuestInfo> getAllQuestInfos() {
        return this.cache.getAll();
    }
    
    public QuestInfo getQuestInfo(String name) {
        return this.cache.get(name);
    }
}