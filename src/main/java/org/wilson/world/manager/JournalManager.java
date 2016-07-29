package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Journal;

public class JournalManager implements ItemTypeProvider {
    public static final String NAME = "journal";
    
    private static JournalManager instance;
    
    private DAO<Journal> dao = null;
    
    @SuppressWarnings("unchecked")
    private JournalManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Journal.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static JournalManager getInstance() {
        if(instance == null) {
            instance = new JournalManager();
        }
        return instance;
    }
    
    public void createJournal(Journal journal) {
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

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
