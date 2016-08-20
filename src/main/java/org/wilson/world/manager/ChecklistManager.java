package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Checklist;
import org.wilson.world.model.ChecklistDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ChecklistManager implements ItemTypeProvider, EventListener {
    public static final String NAME = "checklist";
    
    private static ChecklistManager instance;
    
    private DAO<Checklist> dao = null;
    
    @SuppressWarnings("unchecked")
    private ChecklistManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Checklist.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        EventManager.getInstance().registerListener(EventType.UpdateChecklist, this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Checklist checklist : getChecklists()) {
                    boolean found = checklist.name.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = checklist.id;
                        content.name = checklist.name;
                        content.description = checklist.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ChecklistManager getInstance() {
        if(instance == null) {
            instance = new ChecklistManager();
        }
        return instance;
    }
    
    public void createChecklist(Checklist checklist) {
        ItemManager.getInstance().checkDuplicate(checklist);
        
        this.dao.create(checklist);
    }
    
    public Checklist getChecklist(int id) {
        Checklist checklist = this.dao.get(id);
        if(checklist != null) {
            this.loadChecklist(checklist);
            return checklist;
        }
        else {
            return null;
        }
    }
    
    private void loadChecklist(Checklist checklist) {
        String progress = checklist.progress;
        if(StringUtils.isBlank(progress)) {
            return;
        }
        
        progress = progress.trim();
        List<Integer> checked = new ArrayList<Integer>();
        for(String item : progress.split(",")) {
            try {
                checked.add(Integer.parseInt(item.trim()));
            }
            catch(Exception e) {
            }
        }
        
        checklist.checked = checked;
    }
    
    public List<Checklist> getChecklists() {
        List<Checklist> result = new ArrayList<Checklist>();
        for(Checklist checklist : this.dao.getAll()) {
            this.loadChecklist(checklist);
            result.add(checklist);
        }
        return result;
    }
    
    public void updateChecklist(Checklist checklist) {
        this.dao.update(checklist);
    }
    
    public void deleteChecklist(int id) {
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
        return target instanceof Checklist;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Checklist checklist = (Checklist)target;
        return String.valueOf(checklist.id);
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
        
        Checklist checklist = (Checklist)target;
        return checklist.name;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.UpdateChecklist.equals(event.type)) {
            Checklist checklist = (Checklist) event.data.get("new_data");
            if(checklist != null) {
                checklist = this.getChecklist(checklist.id);
                ChecklistDef def = ChecklistDefManager.getInstance().getChecklistDef(checklist.defId);
                if(def != null) {
                    if(def.items.size() == checklist.checked.size()) {
                        this.deleteChecklist(checklist.id);
                    }
                }
            }
        }
    }
}
