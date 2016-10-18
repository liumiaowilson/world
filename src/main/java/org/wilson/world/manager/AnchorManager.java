package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.anchor.AnchorIdeaConverter;
import org.wilson.world.anchor.AnchorType;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Anchor;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class AnchorManager implements ItemTypeProvider {
    public static final String NAME = "anchor";
    
    private static AnchorManager instance;
    
    private DAO<Anchor> dao = null;
    
    private List<String> anchorTypes = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private AnchorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Anchor.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Anchor anchor : getAnchors()) {
                    boolean found = anchor.name.contains(text) || anchor.stimuli.contains(text) || anchor.response.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = anchor.id;
                        content.name = anchor.name;
                        content.description = anchor.stimuli;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new AnchorIdeaConverter());
        
        this.loadAnchorTypes();
    }
    
    private void loadAnchorTypes() {
        for(AnchorType type : AnchorType.values()) {
            this.anchorTypes.add(type.name());
        }
    }
    
    public static AnchorManager getInstance() {
        if(instance == null) {
            instance = new AnchorManager();
        }
        return instance;
    }
    
    public void createAnchor(Anchor anchor) {
        ItemManager.getInstance().checkDuplicate(anchor);
        
        this.dao.create(anchor);
    }
    
    public Anchor getAnchor(int id) {
        Anchor anchor = this.dao.get(id);
        if(anchor != null) {
            return anchor;
        }
        else {
            return null;
        }
    }
    
    public List<Anchor> getAnchors() {
        List<Anchor> result = new ArrayList<Anchor>();
        for(Anchor anchor : this.dao.getAll()) {
            result.add(anchor);
        }
        return result;
    }
    
    public void updateAnchor(Anchor anchor) {
        this.dao.update(anchor);
    }
    
    public void deleteAnchor(int id) {
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
        return target instanceof Anchor;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Anchor anchor = (Anchor)target;
        return String.valueOf(anchor.id);
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
        
        Anchor anchor = (Anchor)target;
        return anchor.name;
    }
    
    public List<String> getAnchorTypes() {
        return Collections.unmodifiableList(this.anchorTypes);
    }
}
