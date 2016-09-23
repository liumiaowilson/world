package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Journal;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class JournalManager implements ItemTypeProvider {
    public static final String NAME = "journal";
    
    private static JournalManager instance;
    
    private DAO<Journal> dao = null;
    
    @SuppressWarnings("unchecked")
    private JournalManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Journal.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Journal journal : getJournals()) {
                    boolean found = journal.name.contains(text) || journal.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = journal.id;
                        content.name = journal.name;
                        content.description = journal.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static JournalManager getInstance() {
        if(instance == null) {
            instance = new JournalManager();
        }
        return instance;
    }
    
    public void createJournal(Journal journal) {
        ItemManager.getInstance().checkDuplicate(journal);
        
        this.dao.create(journal);
    }
    
    public Journal getJournal(int id) {
        Journal journal = this.dao.get(id);
        if(journal != null) {
            return journal;
        }
        else {
            return null;
        }
    }
    
    public List<Journal> getJournals() {
        List<Journal> result = new ArrayList<Journal>();
        for(Journal journal : this.dao.getAll()) {
            result.add(journal);
        }
        return result;
    }
    
    public void updateJournal(Journal journal) {
        this.dao.update(journal);
    }
    
    public void deleteJournal(int id) {
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
        return target instanceof Journal;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Journal journal = (Journal)target;
        return String.valueOf(journal.id);
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
        
        Journal journal = (Journal)target;
        return journal.name;
    }
    
    public Journal getLastCreatedJournal() {
        List<Journal> journals = this.getJournals();
        if(journals.isEmpty()) {
            return null;
        }
        
        Collections.sort(journals, new Comparator<Journal>(){

            @Override
            public int compare(Journal o1, Journal o2) {
                return Long.compare(o1.time, o2.time);
            }
            
        });
        
        return journals.get(journals.size() - 1);
    }
}
