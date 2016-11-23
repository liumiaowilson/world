package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.image.ImageRef;
import org.wilson.world.image.ImageSetImageContributor;
import org.wilson.world.image.ImageSetInfo;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ImageSet;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;

public class ImageSetManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(ImageSetManager.class);
	
    public static final String NAME = "image_set";
    
    private static ImageSetManager instance;
    
    private static String sampleContent;
    
    private DAO<ImageSet> dao = null;
    
    @SuppressWarnings("unchecked")
    private ImageSetManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ImageSet.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        ImageSetImageContributor contributor = new ImageSetImageContributor();
        ((CachedDAO<ImageSet>)this.dao).getCache().addCacheListener(contributor);
        ImageManager.getInstance().addImageContributor(contributor);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ImageSet set : getImageSets()) {
                    boolean found = set.name.contains(text) || set.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = set.id;
                        content.name = set.name;
                        content.description = set.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ImageSetManager getInstance() {
        if(instance == null) {
            instance = new ImageSetManager();
        }
        return instance;
    }
    
    public void createImageSet(ImageSet set) {
        ItemManager.getInstance().checkDuplicate(set);
        
        if(this.willCreateLoop(set)) {
        	throw new DataException("Failed to create image set because of loops");
        }
        
        this.dao.create(set);
    }
    
    private ImageSet loadImageSet(ImageSet set) {
    	if(set == null) {
    		return null;
    	}
    	
    	try {
    		set.refs.clear();
    		JSONArray array = JSONArray.fromObject(set.content);
    		for(int i = 0; i < array.size(); i++) {
    			set.refs.add(array.getString(i).trim());
    		}
    	}
    	catch(Exception e) {
    		logger.error(e);
    	}
    	
    	return set;
    }
    
    public String toImageSetContent(List<String> refs) {
    	if(refs == null) {
    		return null;
    	}
    	
    	JSONArray array = new JSONArray();
    	for(String ref : refs) {
    		array.add(ref);
    	}
    	
    	return array.toString();
    }
    
    public ImageSet getImageSet(int id) {
    	ImageSet set = this.dao.get(id);
        if(set != null) {
        	set = this.loadImageSet(set);
            return set;
        }
        else {
            return null;
        }
    }
    
    public ImageSet getImageSet(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(ImageSet set : this.getImageSets()) {
    		if(name.equals(set.name)) {
    			return set;
    		}
    	}
    	
    	return null;
    }
    
    public List<ImageSet> getImageSets() {
        List<ImageSet> result = new ArrayList<ImageSet>();
        for(ImageSet set : this.dao.getAll()) {
        	set = this.loadImageSet(set);
            result.add(set);
        }
        return result;
    }
    
    public void updateImageSet(ImageSet set) {
    	if(this.willCreateLoop(set)) {
        	throw new DataException("Failed to create image set because of loops");
        }
    	
        this.dao.update(set);
    }
    
    public void deleteImageSet(int id) {
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
        return target instanceof ImageSet;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ImageSet set = (ImageSet)target;
        return String.valueOf(set.id);
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
        
        ImageSet set = (ImageSet)target;
        return set.name;
    }
    
    public static String getSampleContent() throws IOException {
        if(sampleContent == null) {
            InputStream is = ImageSetManager.class.getClassLoader().getResourceAsStream("image_set_content.json");
            try {
                sampleContent = IOUtils.toString(is);
            }
            finally {
                if(is != null) {
                    is.close();
                }
            }
        }
        return sampleContent;
    }
    
    public boolean willCreateLoop(ImageSet set) {
    	if(set == null) {
    		return false;
    	}
    	set = this.loadImageSet(set);
    	
    	List<ImageSet> children = this.getChildrenImageSets(set);
    	for(ImageSet child : children) {
    		if(this.contains(child, set)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public boolean contains(ImageSet set1, ImageSet set2) {
    	if(set1 == null || set2 == null) {
    		return false;
    	}
    	
    	String refName = ImageSetImageContributor.toRefName(set2.name);
    	
    	if(set1.refs.contains(refName)) {
    		return true;
    	}
    	
    	List<ImageSet> children = this.getChildrenImageSets(set1);
    	for(ImageSet child : children) {
    		if(this.contains(child, set2)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public List<ImageSet> getChildrenImageSets(ImageSet set) {
    	if(set == null) {
    		return Collections.emptyList();
    	}
    	
    	List<ImageSet> sets = new ArrayList<ImageSet>();
    	for(String ref : set.refs) {
    		if(ImageSetImageContributor.isImageSetImageRef(ref)) {
    			String name = ImageSetImageContributor.fromRefName(ref);
    			ImageSet child = this.getImageSet(name);
    			sets.add(child);
    		}
    	}
    	
    	return sets;
    }
    
    public List<ImageRef> getImageRefs(ImageSet set) {
    	if(set == null) {
    		return Collections.emptyList();
    	}
    	
    	List<ImageRef> refs = new ArrayList<ImageRef>();
    	for(String r : set.refs) {
    		ImageRef ref = ImageManager.getInstance().getImageRef(r);
    		if(ref != null) {
    			refs.add(ref);
    		}
    	}
    	
    	return refs;
    }
    
    /**
     * Get direct enclosing image sets
     * 
     * @param refName
     * @return
     */
    public List<ImageSet> getEnclosingImageSets(String refName) {
    	if(StringUtils.isBlank(refName)) {
    		return Collections.emptyList();
    	}
    	
    	List<ImageSet> sets = new ArrayList<ImageSet>();
    	for(ImageSet set : this.getImageSets()) {
    		if(set.refs.contains(refName)) {
    			sets.add(set);
    		}
    	}
    	
    	return sets;
    }
    
    public int getNumOfImages(ImageSet set) {
    	if(set == null) {
    		return 0;
    	}
    	
    	int total = 0;
    	for(String ref : set.refs) {
    		if(ImageSetImageContributor.isImageSetImageRef(ref)) {
    			String name = ImageSetImageContributor.fromRefName(ref);
    			ImageSet child = this.getImageSet(name);
    			total += this.getNumOfImages(child);
    		}
    		else {
    			total += 1;
    		}
    	}
    	
    	return total;
    }
    
    public List<ImageSetInfo> getImageSetInfos() {
    	List<ImageSetInfo> infos = new ArrayList<ImageSetInfo>();
    	
    	for(ImageSet set : this.getImageSets()) {
    		ImageSetInfo info = new ImageSetInfo();
    		info.id = set.id;
    		info.name = set.name;
    		info.count = this.getNumOfImages(set);
    		infos.add(info);
    	}
    	
    	return infos;
    }
}
