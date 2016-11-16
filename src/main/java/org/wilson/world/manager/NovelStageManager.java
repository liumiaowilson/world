package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.graph.Edge;
import org.wilson.world.graph.Node;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelStage;
import org.wilson.world.novel.NovelStageStatus;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelStageManager implements ItemTypeProvider {
    public static final String NAME = "novel_stage";
    
    private static NovelStageManager instance;
    
    private DAO<NovelStage> dao = null;
    
    private Map<Integer, Set<Integer>> graph = null;
    
    private List<String> statuses = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private NovelStageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelStage.class);
        this.graph = new HashMap<Integer, Set<Integer>>();
        ((CachedDAO<NovelStage>)this.dao).getCache().addCacheListener(new CacheListener<NovelStage>(){

			@Override
			public void cachePut(NovelStage old, NovelStage v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				int prev = v.previousId;
				int curr = v.id;
				Set<Integer> ids = NovelStageManager.this.graph.get(prev);
				if(ids == null) {
					ids = new HashSet<Integer>();
					NovelStageManager.this.graph.put(prev, ids);
				}
				ids.add(curr);
			}

			@Override
			public void cacheDeleted(NovelStage v) {
				int prev = v.previousId;
				int curr = v.id;
				Set<Integer> ids = NovelStageManager.this.graph.get(prev);
				if(ids != null) {
					ids.remove(curr);
				}
			}

			@Override
			public void cacheLoaded(List<NovelStage> all) {
			}

			@Override
			public void cacheLoading(List<NovelStage> old) {
				NovelStageManager.this.graph.clear();
			}
        	
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelStage stage : getNovelStages()) {
                    boolean found = stage.name.contains(text) || stage.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = stage.id;
                        content.name = stage.name;
                        content.description = stage.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.initStatuses();
    }
    
    private void initStatuses() {
    	for(NovelStageStatus status : NovelStageStatus.values()) {
    		this.statuses.add(status.name());
    	}
    }
    
    public static NovelStageManager getInstance() {
        if(instance == null) {
            instance = new NovelStageManager();
        }
        return instance;
    }
    
    public void createNovelStage(NovelStage stage) {
        ItemManager.getInstance().checkDuplicate(stage);
        
        this.dao.create(stage);
    }
    
    public NovelStage getNovelStage(int id) {
    	NovelStage stage = this.dao.get(id);
        if(stage != null) {
            return stage;
        }
        else {
            return null;
        }
    }
    
    public List<NovelStage> getNovelStages() {
        List<NovelStage> result = new ArrayList<NovelStage>();
        for(NovelStage stage : this.dao.getAll()) {
            result.add(stage);
        }
        return result;
    }
    
    public void updateNovelStage(NovelStage stage) {
        this.dao.update(stage);
    }
    
    public void deleteNovelStage(int id) {
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
        return target instanceof NovelStage;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelStage stage = (NovelStage)target;
        return String.valueOf(stage.id);
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
        
        NovelStage stage = (NovelStage)target;
        return stage.name;
    }
    
    public Map<Integer, Node> getNovelStageNodes() {
    	Map<Integer, Node> nodes = new HashMap<Integer, Node>();
    	
    	for(NovelStage stage : this.getNovelStages()) {
    		Node node = new Node();
    		node.id = String.valueOf(stage.id);
    		node.name = stage.name;
    		nodes.put(stage.id, node);
    	}
    	
    	return nodes;
    }
    
    public List<Edge> getNovelStageEdges(Map<Integer, Node> nodes) {
    	List<Edge> edges = new ArrayList<Edge>();
    	
    	for(Entry<Integer, Set<Integer>> entry : this.graph.entrySet()) {
    		int id = entry.getKey();
    		Set<Integer> afterIds = entry.getValue();
    		for(Integer afterId : afterIds) {
    			Edge edge = new Edge();
    			edge.id = id + "_" + afterId;
    			edge.source = nodes.get(id);
    			edge.target = nodes.get(afterId);
    			if(edge.source == null || edge.target == null) {
    				continue;
    			}
    			edges.add(edge);
    		}
    	}
    	
    	return edges;
    }
    
    /**
     * Get first staget with no previous stage
     * 
     * @return
     */
    public NovelStage getStartStage() {
    	for(NovelStage stage : this.getNovelStages()) {
    		NovelStage prev = this.getNovelStage(stage.previousId);
    		if(prev == null) {
    			return stage;
    		}
    	}
    	
    	return null;
    }
    
    public List<NovelStage> getFollowingStages(NovelStage stage) {
    	if(stage == null) {
    		return Collections.emptyList();
    	}
    	
    	List<NovelStage> stages = new ArrayList<NovelStage>();
    	Set<Integer> ids = this.graph.get(stage.id);
    	if(ids != null) {
    		for(Integer id : ids) {
        		NovelStage following = this.getNovelStage(id);
        		if(following != null) {
        			stages.add(following);
        		}
        	}
    	}
    	
    	return stages;
    }
    
    /**
     * Get a random following stage
     * 
     * @param stage
     * @return
     */
    public NovelStage getFollowingStage(NovelStage stage) {
    	if(stage == null) {
    		return null;
    	}
    	
    	List<NovelStage> stages = this.getFollowingStages(stage);
    	if(stages.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(stages.size());
    	return stages.get(n);
    }
    
    public boolean isStartStage(NovelStage stage) {
    	if(stage == null) {
    		return false;
    	}
    	
    	NovelStage prev = this.getNovelStage(stage.previousId);
    	return prev == null;
    }
    
    public boolean isEndStage(NovelStage stage) {
    	if(stage == null) {
    		return false;
    	}
    	
    	return this.getFollowingStage(stage) == null;
    }
    
    public List<String> getStatuses() {
    	return Collections.unmodifiableList(this.statuses);
    }
    
    public boolean isRequiredStage(NovelStage stage) {
    	if(stage == null) {
    		return false;
    	}
    	
    	return NovelStageStatus.Required.name().equals(stage.status);
    }
}
