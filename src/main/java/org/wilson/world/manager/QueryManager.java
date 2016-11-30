package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.flashcard.FlashCardQueryProcessor;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.model.Query;
import org.wilson.world.query.DefaultQueryProcessor;
import org.wilson.world.query.IdeaAndTaskQueryProcessor;
import org.wilson.world.query.QueryHandler;
import org.wilson.world.query.QueryProcessor;
import org.wilson.world.query.StarredQueryProcessor;
import org.wilson.world.query.SystemQueryProcessor;
import org.wilson.world.quest.QuestGroupQueryProcessor;
import org.wilson.world.task.IncompleteTaskQueryProcessor;
import org.wilson.world.task.ReviewTaskQueryProcessor;
import org.wilson.world.task.SmallTaskQueryProcessor;

public class QueryManager implements ItemTypeProvider, JavaExtensionListener<SystemQueryProcessor> {
    public static final String NAME = "query";
    
    private static QueryManager instance;
    
    private DAO<Query> dao = null;
    
    private Cache<String, QueryProcessor> cache = null;
    private Cache<Integer, QueryProcessor> idCache = null;
    
    private static int GLOBAL_ID = 1;
    
    @SuppressWarnings("unchecked")
    private QueryManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Query.class);
        this.cache = new DefaultCache<String, QueryProcessor>("query_manager_cache", false);
        this.idCache = new DefaultCache<Integer, QueryProcessor>("query_manager_id_cache", false);
        ((CachedDAO<Query>)this.dao).getCache().addCacheListener(new CacheListener<Query>(){

            @Override
            public void cachePut(Query old, Query v) {
                if(old != null) {
                    QueryManager.this.cache.delete(old.name);
                    QueryManager.this.idCache.delete(old.id);
                }
                loadQuery(v);
            }

            @Override
            public void cacheDeleted(Query v) {
                QueryManager.this.cache.delete(v.name);
                QueryManager.this.idCache.delete(v.id);
            }

            @Override
            public void cacheLoaded(List<Query> all) {
                loadSystemQueryProcessors();
            }

            @Override
            public void cacheLoading(List<Query> old) {
                QueryManager.this.cache.clear();
                QueryManager.this.idCache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ExtManager.getInstance().addJavaExtensionListener(this);
    }
    
    private void loadSystemQueryProcessors() {
        GLOBAL_ID = 1;
        this.loadSystemQueryProcessor(new StarredQueryProcessor());
        this.loadSystemQueryProcessor(new IncompleteTaskQueryProcessor());
        this.loadSystemQueryProcessor(new SmallTaskQueryProcessor());
        this.loadSystemQueryProcessor(new QuestGroupQueryProcessor());
        this.loadSystemQueryProcessor(new FlashCardQueryProcessor());
        this.loadSystemQueryProcessor(new IdeaAndTaskQueryProcessor());
        this.loadSystemQueryProcessor(new ReviewTaskQueryProcessor());
    }
    
    private void loadSystemQueryProcessor(SystemQueryProcessor processor) {
        processor.setID(-GLOBAL_ID++);
        this.cache.put(processor.getName(), processor);
        this.idCache.put(processor.getID(), processor);
    }
    
    private void removeSystemQueryProcessor(SystemQueryProcessor processor) {
    	if(processor != null) {
    		this.cache.delete(processor.getName());
    		this.idCache.delete(processor.getID());
    	}
    }
    
    private void loadQuery(Query query) {
        if(query == null) {
            return;
        }
        
        String impl = query.impl;
        QueryHandler handler = (QueryHandler) ExtManager.getInstance().getExtension(impl, QueryHandler.class);
        
        DefaultQueryProcessor processor = new DefaultQueryProcessor(query, handler);
        this.cache.put(processor.getName(), processor);
        this.idCache.put(processor.getID(), processor);
    }
    
    public static QueryManager getInstance() {
        if(instance == null) {
            instance = new QueryManager();
        }
        return instance;
    }
    
    public void createQuery(Query query) {
        ItemManager.getInstance().checkDuplicate(query);
        
        this.dao.create(query);
    }
    
    public Query getQuery(int id) {
        Query query = this.dao.get(id);
        if(query != null) {
            return query;
        }
        else {
            return null;
        }
    }
    
    public List<Query> getQueries() {
        List<Query> result = new ArrayList<Query>();
        for(Query query : this.dao.getAll()) {
            result.add(query);
        }
        return result;
    }
    
    public void updateQuery(Query query) {
        this.dao.update(query);
    }
    
    public void deleteQuery(int id) {
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
        return target instanceof Query;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Query query = (Query)target;
        return String.valueOf(query.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<QueryProcessor> getQueryProcessors() {
        return this.cache.getAll();
    }
    
    public QueryProcessor getQueryProcessor(String name) {
        return this.cache.get(name);
    }
    
    public QueryProcessor getQueryProcessor(int id) {
        return this.idCache.get(id);
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Query query = (Query)target;
        return query.name;
    }

	@Override
	public Class<SystemQueryProcessor> getExtensionClass() {
		return SystemQueryProcessor.class;
	}

	@Override
	public void created(SystemQueryProcessor t) {
		this.loadSystemQueryProcessor(t);
	}

	@Override
	public void removed(SystemQueryProcessor t) {
		this.removeSystemQueryProcessor(t);
	}
}
