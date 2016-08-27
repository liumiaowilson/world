package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.flashcard.FlashCardDBCleaner;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.FlashCard;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class FlashCardManager implements ItemTypeProvider {
    public static final String NAME = "flashcard";
    
    private static FlashCardManager instance;
    
    private DAO<FlashCard> dao = null;
    
    @SuppressWarnings("unchecked")
    private FlashCardManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(FlashCard.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new FlashCardDBCleaner());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(FlashCard card : getFlashCards()) {
                    boolean found = card.name.contains(text) || card.top.contains(text) || card.bottom.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = card.id;
                        content.name = card.name;
                        content.description = card.top;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FlashCardManager getInstance() {
        if(instance == null) {
            instance = new FlashCardManager();
        }
        return instance;
    }
    
    public void createFlashCard(FlashCard card) {
        ItemManager.getInstance().checkDuplicate(card);
        
        this.dao.create(card);
    }
    
    public FlashCard getFlashCard(int id) {
        FlashCard card = this.dao.get(id);
        if(card != null) {
            return card;
        }
        else {
            return null;
        }
    }
    
    public List<FlashCard> getFlashCards() {
        List<FlashCard> result = new ArrayList<FlashCard>();
        for(FlashCard card : this.dao.getAll()) {
            result.add(card);
        }
        return result;
    }
    
    public List<FlashCard> getFlashCardsBySet(int setId) {
        List<FlashCard> ret = new ArrayList<FlashCard>();
        
        for(FlashCard card : this.getFlashCards()) {
            if(card.setId == setId) {
                ret.add(card);
            }
        }
        
        return ret;
    }
    
    public void updateFlashCard(FlashCard card) {
        this.dao.update(card);
    }
    
    public void deleteFlashCard(int id) {
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
        return target instanceof FlashCard;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        FlashCard card = (FlashCard)target;
        return String.valueOf(card.id);
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
        
        FlashCard card = (FlashCard)target;
        return card.setId + "_" + card.name;
    }
}
