package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.QuestDef;

public class QuestDefManager implements ItemTypeProvider {
    public static final String NAME = "quest_def";
    
    private static QuestDefManager instance;
    
    private DAO<QuestDef> dao = null;
    
    @SuppressWarnings("unchecked")
    private QuestDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(QuestDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static QuestDefManager getInstance() {
        if(instance == null) {
            instance = new QuestDefManager();
        }
        return instance;
    }
    
    public void createQuestDef(QuestDef def) {
        this.dao.create(def);
    }
    
    public QuestDef getQuestDef(int id) {
        QuestDef def = this.dao.get(id);
        if(def != null) {
            return def;
        }
        else {
            return null;
        }
    }
    
    public List<QuestDef> getQuestDefs() {
        List<QuestDef> result = new ArrayList<QuestDef>();
        for(QuestDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public void updateQuestDef(QuestDef def) {
        this.dao.update(def);
    }
    
    public void deleteQuestDef(int id) {
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
        return target instanceof QuestDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        QuestDef def = (QuestDef)target;
        return String.valueOf(def.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}