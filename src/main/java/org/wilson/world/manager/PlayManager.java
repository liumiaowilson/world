package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Play;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PlayManager implements ItemTypeProvider {
    public static final String NAME = "play";
    
    private static PlayManager instance;
    
    private DAO<Play> dao = null;
    
    @SuppressWarnings("unchecked")
    private PlayManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Play.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Play play : getPlays()) {
                    boolean found = play.name.contains(text) || play.content.contains(text) || play.source.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = play.id;
                        content.name = play.name;
                        content.description = play.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PlayManager getInstance() {
        if(instance == null) {
            instance = new PlayManager();
        }
        return instance;
    }
    
    public void createPlay(Play play) {
        ItemManager.getInstance().checkDuplicate(play);
        
        this.dao.create(play);
    }
    
    public Play getPlay(int id) {
    	Play play = this.dao.get(id);
        if(play != null) {
            return play;
        }
        else {
            return null;
        }
    }
    
    public List<Play> getPlays() {
        List<Play> result = new ArrayList<Play>();
        for(Play play : this.dao.getAll()) {
            result.add(play);
        }
        return result;
    }
    
    public void updatePlay(Play play) {
        this.dao.update(play);
    }
    
    public void deletePlay(int id) {
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
        return target instanceof Play;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Play play = (Play)target;
        return String.valueOf(play.id);
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
        
        Play play = (Play)target;
        return play.name;
    }
    
    public Play randomPlay() {
    	List<Play> plays = this.getPlays();
    	if(plays.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(plays.size());
    	return plays.get(n);
    }
}
