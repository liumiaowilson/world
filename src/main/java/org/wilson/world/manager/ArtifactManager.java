package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Artifact;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ArtifactManager implements ItemTypeProvider {
    public static final String NAME = "artifact";
    
    private static ArtifactManager instance;
    
    private DAO<Artifact> dao = null;
    
    @SuppressWarnings("unchecked")
    private ArtifactManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Artifact.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Artifact artifact : getArtifacts()) {
                    boolean found = artifact.name.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = artifact.id;
                        content.name = artifact.name;
                        content.description = artifact.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ArtifactManager getInstance() {
        if(instance == null) {
            instance = new ArtifactManager();
        }
        return instance;
    }
    
    public void createArtifact(Artifact artifact) {
        ItemManager.getInstance().checkDuplicate(artifact);
        
        this.dao.create(artifact);
    }
    
    public Artifact getArtifact(int id, boolean lazy) {
        Artifact artifact = this.dao.get(id, lazy);
        if(artifact != null) {
            return artifact;
        }
        else {
            return null;
        }
    }
    
    public Artifact getArtifact(int id) {
        return this.getArtifact(id, true);
    }
    
    public Artifact getArtifact(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(Artifact artifact : this.getArtifacts()) {
            if(name.equals(artifact.name)) {
                return artifact;
            }
        }
        
        return null;
    }
    
    public List<Artifact> getArtifacts() {
        List<Artifact> result = new ArrayList<Artifact>();
        for(Artifact artifact : this.dao.getAll()) {
            result.add(artifact);
        }
        return result;
    }
    
    public void updateArtifact(Artifact artifact) {
        this.dao.update(artifact);
    }
    
    public void deleteArtifact(int id) {
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
        return target instanceof Artifact;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Artifact artifact = (Artifact)target;
        return String.valueOf(artifact.id);
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
        
        Artifact artifact = (Artifact)target;
        return artifact.name;
    }
    
    public Artifact randomArtifact() {
        List<Artifact> artifacts = this.getArtifacts();
        if(artifacts.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(artifacts.size());
        return artifacts.get(n);
    }
}
