package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Chat;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ChatManager implements ItemTypeProvider {
    public static final String NAME = "chat";
    
    private static ChatManager instance;
    
    private DAO<Chat> dao = null;
    
    @SuppressWarnings("unchecked")
    private ChatManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Chat.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Chat chat : getChats()) {
                    boolean found = chat.name.contains(text) || chat.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = chat.id;
                        content.name = chat.name;
                        content.description = chat.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ChatManager getInstance() {
        if(instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }
    
    public void createChat(Chat chat) {
        ItemManager.getInstance().checkDuplicate(chat);
        
        this.dao.create(chat);
    }
    
    public Chat getChat(int id) {
        Chat chat = this.dao.get(id);
        if(chat != null) {
            return chat;
        }
        else {
            return null;
        }
    }
    
    public List<Chat> getChats() {
        List<Chat> result = new ArrayList<Chat>();
        for(Chat chat : this.dao.getAll()) {
            result.add(chat);
        }
        return result;
    }
    
    public void updateChat(Chat chat) {
        this.dao.update(chat);
    }
    
    public void deleteChat(int id) {
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
        return target instanceof Chat;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Chat chat = (Chat)target;
        return String.valueOf(chat.id);
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
        
        Chat chat = (Chat)target;
        return chat.name;
    }
    
    public Chat randomChat() {
    	List<Chat> chats = this.getChats();
    	if(chats.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(chats.size());
    	return chats.get(n);
    }
}
