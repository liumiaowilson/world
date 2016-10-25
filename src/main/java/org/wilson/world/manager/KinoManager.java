package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.kino.KinoPart;
import org.wilson.world.model.Kino;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class KinoManager implements ItemTypeProvider {
    public static final String NAME = "kino";
    
    private static KinoManager instance;
    
    private DAO<Kino> dao = null;
    
    private List<String> kinoTypes = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private KinoManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Kino.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Kino kino : getKinos()) {
                    boolean found = kino.name.contains(text) || kino.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = kino.id;
                        content.name = kino.name;
                        content.description = kino.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.initKinoTypes();
    }
    
    private void initKinoTypes() {
        for(KinoPart part : KinoPart.values()) {
            this.kinoTypes.add(part.name());
        }
    }
    
    public static KinoManager getInstance() {
        if(instance == null) {
            instance = new KinoManager();
        }
        return instance;
    }
    
    public void createKino(Kino kino) {
        ItemManager.getInstance().checkDuplicate(kino);
        
        this.dao.create(kino);
    }
    
    public Kino getKino(int id) {
        Kino kino = this.dao.get(id);
        if(kino != null) {
            return kino;
        }
        else {
            return null;
        }
    }
    
    public List<Kino> getKinos() {
        List<Kino> result = new ArrayList<Kino>();
        for(Kino kino : this.dao.getAll()) {
            result.add(kino);
        }
        return result;
    }
    
    public void updateKino(Kino kino) {
        this.dao.update(kino);
    }
    
    public void deleteKino(int id) {
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
        return target instanceof Kino;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Kino kino = (Kino)target;
        return String.valueOf(kino.id);
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
        
        Kino kino = (Kino)target;
        return kino.name;
    }
    
    public List<String> getKinoTypes() {
        return Collections.unmodifiableList(this.kinoTypes);
    }
    
    public Kino randomKino() {
    	List<Kino> kinos = this.getKinos();
    	if(kinos.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(kinos.size());
    	return kinos.get(n);
    }
}
