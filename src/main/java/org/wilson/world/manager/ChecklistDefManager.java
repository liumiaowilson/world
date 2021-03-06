package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.checklist.ChecklistDefItem;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ChecklistDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ChecklistDefManager implements ItemTypeProvider {
    public static final String NAME = "checklist_def";
    
    private static ChecklistDefManager instance;
    
    private DAO<ChecklistDef> dao = null;
    
    private static String sampleContent = null;
    
    @SuppressWarnings("unchecked")
    private ChecklistDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ChecklistDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ChecklistDef def : getChecklistDefs()) {
                    boolean found = def.name.contains(text) || def.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = def.id;
                        content.name = def.name;
                        content.description = def.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ChecklistDefManager getInstance() {
        if(instance == null) {
            instance = new ChecklistDefManager();
        }
        return instance;
    }
    
    public void createChecklistDef(ChecklistDef def) {
        ItemManager.getInstance().checkDuplicate(def);
        
        this.dao.create(def);
    }
    
    public ChecklistDef getChecklistDef(int id) {
        ChecklistDef def = this.dao.get(id);
        if(def != null) {
            this.loadChecklistDef(def);
            return def;
        }
        else {
            return null;
        }
    }
    
    private void loadChecklistDef(ChecklistDef def) {
        def.items = toChecklistDefItems(def.content);
    }
    
    public List<ChecklistDef> getChecklistDefs() {
        List<ChecklistDef> result = new ArrayList<ChecklistDef>();
        for(ChecklistDef def : this.dao.getAll()) {
            this.loadChecklistDef(def);
            result.add(def);
        }
        return result;
    }
    
    public void updateChecklistDef(ChecklistDef def) {
        this.dao.update(def);
    }
    
    public void deleteChecklistDef(int id) {
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
        return target instanceof ChecklistDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ChecklistDef def = (ChecklistDef)target;
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
        
        ChecklistDef def = (ChecklistDef)target;
        return def.name;
    }
    
    public static String getSampleContent() throws IOException {
        if(sampleContent == null) {
            InputStream is = ChecklistDefManager.class.getClassLoader().getResourceAsStream("checklist_def_content.json");
            try {
                sampleContent = IOUtils.toString(is);
            }
            finally {
                if(is != null) {
                    is.close();
                }
            }
        }
        return sampleContent;
    }
    
    public static List<ChecklistDefItem> toChecklistDefItems(String content) {
        if(StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        
        List<ChecklistDefItem> items = new ArrayList<ChecklistDefItem>();
        JSONArray array = JSONArray.fromObject(content);
        for(int i = 0; i < array.size(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                ChecklistDefItem item = new ChecklistDefItem();
                item.id = obj.getInt("id");
                item.name = obj.getString("name");
                items.add(item);
            }
            catch(Exception e) {
            }
        }
        
        return items;
    }
    
    public static String fromChecklistDefItems(List<ChecklistDefItem> items) {
        JSONArray array = new JSONArray();
        
        if(items != null && !items.isEmpty()) {
            for(ChecklistDefItem item : items) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.id);
                obj.put("name", item.name);
                array.add(obj);
            }
        }
        
        return array.toString();
    }
}
