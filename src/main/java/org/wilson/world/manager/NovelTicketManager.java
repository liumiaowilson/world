package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelTicket;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.novel.NovelTicketMonitor;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelTicketManager implements ItemTypeProvider {
    public static final String NAME = "novel_ticket";
    
    private static NovelTicketManager instance;
    
    private DAO<NovelTicket> dao = null;
    
    private NovelTicketMonitor monitor = null;
    
    @SuppressWarnings("unchecked")
    private NovelTicketManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelTicket.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelTicket ticket : getNovelTickets()) {
                    boolean found = ticket.name.contains(text) || ticket.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = ticket.id;
                        content.name = ticket.name;
                        content.description = ticket.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.monitor = new NovelTicketMonitor();
        MonitorManager.getInstance().registerMonitorParticipant(this.monitor);
    }
    
    public static NovelTicketManager getInstance() {
        if(instance == null) {
            instance = new NovelTicketManager();
        }
        return instance;
    }
    
    public void createNovelTicket(NovelTicket ticket) {
        ItemManager.getInstance().checkDuplicate(ticket);
        
        this.dao.create(ticket);
    }
    
    public NovelTicket getNovelTicket(int id) {
    	NovelTicket ticket = this.dao.get(id);
        if(ticket != null) {
            return ticket;
        }
        else {
            return null;
        }
    }
    
    public List<NovelTicket> getNovelTickets() {
        List<NovelTicket> result = new ArrayList<NovelTicket>();
        for(NovelTicket ticket : this.dao.getAll()) {
            result.add(ticket);
        }
        return result;
    }
    
    public void updateNovelTicket(NovelTicket ticket) {
        this.dao.update(ticket);
    }
    
    public void deleteNovelTicket(int id) {
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
        return target instanceof NovelTicket;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelTicket ticket = (NovelTicket)target;
        return String.valueOf(ticket.id);
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
        
        NovelTicket ticket = (NovelTicket)target;
        return ticket.docId + ":" + ticket.name;
    }
    
    public MonitorParticipant getMonitor() {
    	return this.monitor;
    }
}
