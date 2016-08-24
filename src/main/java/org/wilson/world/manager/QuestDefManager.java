package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Quest;
import org.wilson.world.model.QuestDef;
import org.wilson.world.quest.QuestDefIdeaConverter;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class QuestDefManager implements ItemTypeProvider {
    public static final String NAME = "quest_def";
    
    private static QuestDefManager instance;
    
    private DAO<QuestDef> dao = null;
    
    @SuppressWarnings("unchecked")
    private QuestDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(QuestDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new QuestDefIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(QuestDef def : getQuestDefs()) {
                    boolean found = def.name.contains(text) || def.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = def.id;
                        content.name = def.name;
                        content.description = def.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static QuestDefManager getInstance() {
        if(instance == null) {
            instance = new QuestDefManager();
        }
        return instance;
    }
    
    public void createQuestDef(QuestDef def) {
        ItemManager.getInstance().checkDuplicate(def);
        
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
        
        QuestDef def = (QuestDef)target;
        return def.name;
    }
    
    public String achieveQuestDef(int id) {
        QuestDef def = this.getQuestDef(id);
        if(def == null) {
            return "Quest def is not found";
        }
        
        Quest quest = new Quest();
        quest.defId = def.id;
        quest.name = def.name;
        quest.content = def.content;
        quest.time = System.currentTimeMillis();
        
        QuestManager.getInstance().createQuest(quest);
        
        return null;
    }
}
