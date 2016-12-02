package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Message;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class MessageManager implements ItemTypeProvider {
    public static final String NAME = "message";
    
    private static MessageManager instance;
    
    private DAO<Message> dao = null;
    
    @SuppressWarnings("unchecked")
    private MessageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Message.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Message message : getMessages()) {
                    boolean found = message.name.contains(text) || message.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = message.id;
                        content.name = message.name;
                        content.description = message.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static MessageManager getInstance() {
        if(instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }
    
    public void createMessage(Message message) {
        ItemManager.getInstance().checkDuplicate(message);
        
        this.dao.create(message);
    }
    
    public Message getMessage(int id) {
    	Message message = this.dao.get(id);
        if(message != null) {
            return message;
        }
        else {
            return null;
        }
    }
    
    public List<Message> getMessages() {
        List<Message> result = new ArrayList<Message>();
        for(Message message : this.dao.getAll()) {
            result.add(message);
        }
        return result;
    }
    
    public void updateMessage(Message message) {
        this.dao.update(message);
    }
    
    public void deleteMessage(int id) {
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
        return target instanceof Message;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Message message = (Message)target;
        return String.valueOf(message.id);
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
        
        Message message = (Message)target;
        return message.name;
    }
}
