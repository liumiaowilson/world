package org.wilson.world.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.image.ImageListInfo;
import org.wilson.world.image.ImageRef;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ImageList;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

import net.sf.json.JSONArray;

public class ImageListManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(ImageListManager.class);
	
    public static final String NAME = "image_list";
    
    private static ImageListManager instance;
    
    private static String sampleContent;
    
    private DAO<ImageList> dao = null;
    
    @SuppressWarnings("unchecked")
    private ImageListManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ImageList.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ImageList list : getImageLists()) {
                    boolean found = list.name.contains(text) || list.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = list.id;
                        content.name = list.name;
                        content.description = list.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ImageListManager getInstance() {
        if(instance == null) {
            instance = new ImageListManager();
        }
        return instance;
    }
    
    public void createImageList(ImageList list) {
        ItemManager.getInstance().checkDuplicate(list);
        
        this.dao.create(list);
    }
    
    private ImageList loadImageList(ImageList list) {
    	if(list == null) {
    		return null;
    	}
    	
    	try {
    		list.refs.clear();
    		JSONArray array = JSONArray.fromObject(list.content);
    		for(int i = 0; i < array.size(); i++) {
    			list.refs.add(array.getString(i).trim());
    		}
    	}
    	catch(Exception e) {
    		logger.error(e);
    	}
    	
    	return list;
    }
    
    public String toImageListContent(List<String> refs) {
    	if(refs == null) {
    		return null;
    	}
    	
    	JSONArray array = new JSONArray();
    	for(String ref : refs) {
    		array.add(ref);
    	}
    	
    	return array.toString();
    }
    
    public ImageList getImageList(int id) {
    	ImageList list = this.dao.get(id);
        if(list != null) {
        	list = this.loadImageList(list);
            return list;
        }
        else {
            return null;
        }
    }
    
    public ImageList getImageList(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(ImageList list : this.getImageLists()) {
    		if(name.equals(list.name)) {
    			return list;
    		}
    	}
    	
    	return null;
    }
    
    public List<ImageList> getImageLists() {
        List<ImageList> result = new ArrayList<ImageList>();
        for(ImageList list : this.dao.getAll()) {
        	list = this.loadImageList(list);
            result.add(list);
        }
        return result;
    }
    
    public void updateImageList(ImageList list) {
        this.dao.update(list);
    }
    
    public void deleteImageList(int id) {
    	final ImageList list = this.getImageList(id);
    	if(list == null) {
    		return;
    	}
    	
        this.dao.delete(id);
        
        ThreadPoolManager.getInstance().execute(new Runnable(){

			@Override
			public void run() {
				for(String refName : list.refs) {
					ImageRef ref = ImageManager.getInstance().getImageRef(refName);
					if(ref != null) {
						ImageManager.getInstance().deleteImageRef(ref);
					}
				}
			}
        	
        });
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
        return target instanceof ImageList;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ImageList list = (ImageList)target;
        return String.valueOf(list.id);
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
        
        ImageList list = (ImageList)target;
        return list.name;
    }
    
    public static String getSampleContent() throws IOException {
        if(sampleContent == null) {
            sampleContent = "[]";
        }
        return sampleContent;
    }
    
    public List<ImageRef> getImageRefs(ImageList list) {
    	if(list == null) {
    		return Collections.emptyList();
    	}
    	
    	List<ImageRef> refs = new ArrayList<ImageRef>();
    	for(String r : list.refs) {
    		ImageRef ref = ImageManager.getInstance().getImageRef(r);
    		if(ref != null) {
    			refs.add(ref);
    		}
    	}
    	
    	return refs;
    }
    
    public List<ImageListInfo> getImageListInfos() {
    	List<ImageListInfo> infos = new ArrayList<ImageListInfo>();
    	
    	for(ImageList list : this.getImageLists()) {
    		ImageListInfo info = new ImageListInfo();
    		info.id = list.id;
    		info.name = list.name;
    		info.count = list.refs.size();
    		infos.add(info);
    	}
    	
    	return infos;
    }
    
    /**
     * Get direct enclosing image lists
     * 
     * @param refName
     * @return
     */
    public List<ImageList> getEnclosingImageLists(String refName) {
    	if(StringUtils.isBlank(refName)) {
    		return Collections.emptyList();
    	}
    	
    	List<ImageList> lists = new ArrayList<ImageList>();
    	for(ImageList set : this.getImageLists()) {
    		if(set.refs.contains(refName)) {
    			lists.add(set);
    		}
    	}
    	
    	return lists;
    }
    
    public ImageList randomImageList() {
    	List<ImageList> imageLists = this.getImageLists();
    	if(imageLists.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(imageLists.size());
    	return imageLists.get(n);
    }
}
