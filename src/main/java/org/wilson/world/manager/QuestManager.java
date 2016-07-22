package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Quest;

public class QuestManager implements ItemTypeProvider {
    public static final String NAME = "quest";
    
    private static QuestManager instance;
    
    private DAO<Quest> dao = null;
    
    @SuppressWarnings("unchecked")
    private QuestManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Quest.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static QuestManager getInstance() {
        if(instance == null) {
            instance = new QuestManager();
        }
        return instance;
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
}
