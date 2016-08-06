package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Document;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class DocumentManager implements ItemTypeProvider {
    public static final String NAME = "document";
    
    private static DocumentManager instance;
    
    private DAO<Document> dao = null;
    
    @SuppressWarnings("unchecked")
    private DocumentManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Document.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Document document : getDocuments()) {
                    boolean found = document.name.contains(text) || document.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = document.id;
                        content.name = document.name;
                        content.description = document.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static DocumentManager getInstance() {
        if(instance == null) {
            instance = new DocumentManager();
        }
        return instance;
    }
    
    public void createDocument(Document document) {
        ItemManager.getInstance().checkDuplicate(document);
        
        this.dao.create(document);
    }
    
    public Document getDocument(int id) {
        Document document = this.dao.get(id);
        if(document != null) {
            return document;
        }
        else {
            return null;
        }
    }
    
    public List<Document> getDocuments() {
        List<Document> result = new ArrayList<Document>();
        for(Document document : this.dao.getAll()) {
            result.add(document);
        }
        return result;
    }
    
    public void updateDocument(Document document) {
        this.dao.update(document);
    }
    
    public void deleteDocument(int id) {
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
        return target instanceof Document;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Document document = (Document)target;
        return String.valueOf(document.id);
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
        
        Document document = (Document)target;
        return document.name;
    }
}
