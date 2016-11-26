package org.wilson.world.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.manga.DemoMangaCreator;
import org.wilson.world.manga.Manga;
import org.wilson.world.manga.MangaCreator;
import org.wilson.world.manga.MangaImageContributor;
import org.wilson.world.manga.MangaItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;

public class MangaManager implements StorageListener, JavaExtensionListener<MangaCreator> {
	private static final Logger logger = Logger.getLogger(MangaManager.class);
	
	public static final String STORAGE_PREFIX = "/manga/";
	
	public static final String STORAGE_SUFFIX = ".jpg";
	
    private static MangaManager instance;
    
    private Map<String, Manga> mangas = new HashMap<String, Manga>();
    
    private Map<String, MangaCreator> creators = new HashMap<String, MangaCreator>();
    
    private MangaManager() {
        ImageManager.getInstance().addImageContributor(new MangaImageContributor());
        
        StorageManager.getInstance().addStorageListener(this);
        
        ExtManager.getInstance().addJavaExtensionListener(this);
        
        this.initMangaCreators();
    }
    
    private void initMangaCreators() {
    	this.addMangaCreator(new DemoMangaCreator());
    }
    
    public void addMangaCreator(MangaCreator creator) {
    	if(creator != null && creator.getName() != null) {
    		this.creators.put(creator.getName(), creator);
    	}
    }
    
    public void removeMangaCreator(MangaCreator creator) {
    	if(creator != null && creator.getName() != null) {
    		this.creators.remove(creator.getName());
    	}
    }
    
    public static MangaManager getInstance() {
        if(instance == null) {
            instance = new MangaManager();
        }
        
        return instance;
    }
    
    public String getMangaDir() {
        return ConfigManager.getInstance().getDataDir() + "manga/";
    }
    
    public void ensureMangaDir() {
        File mangaDir = new File(this.getMangaDir());
        
        if(!mangaDir.isDirectory()) {
            mangaDir.delete();
        }
        
        if(!mangaDir.exists()) {
            mangaDir.mkdirs();
        }
    }
    
    public void clean() {
        File mangaDir = new File(this.getMangaDir());
        
        for(File old : mangaDir.listFiles()) {
            old.delete();
        }
    }
    
    public int getCurrentPages() {
        File mangaDir = new File(this.getMangaDir());
        
        String [] files = mangaDir.list();
        return files == null ? 0 : files.length;
    }
    
    private Manga loadManga(Manga manga) {
    	if(manga == null) {
    		return null;
    	}
    	
    	Collections.sort(manga.items, new Comparator<MangaItem>(){

			@Override
			public int compare(MangaItem o1, MangaItem o2) {
				return o1.name.compareTo(o2.name);
			}
    		
    	});
    	
    	return manga;
    }
    
    public List<Manga> getMangas() {
    	List<Manga> mangas = new ArrayList<Manga>();
    	for(Manga manga : this.mangas.values()) {
    		manga = this.loadManga(manga);
    		mangas.add(manga);
    	}
    	
    	return mangas;
    }
    
    public Manga getManga(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	Manga manga = this.mangas.get(name);
    	if(manga != null) {
    		manga = this.loadManga(manga);
    		return manga;
    	}
    	else {
    		return null;
    	}
    }
    
    public Manga getManga(int id) {
    	for(Manga manga : this.mangas.values()) {
    		if(manga.id == id) {
    			manga = this.loadManga(manga);
    			return manga;
    		}
    	}
    	
    	return null;
    }
    
    private boolean accept(StorageAsset asset) {
        String name = asset.name;
        
        if(!name.startsWith(STORAGE_PREFIX)) {
            return false;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            return false;
        }
        
        return true;
    }
    
    private MangaItem toMangaItem(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        MangaItem item = new MangaItem();
        item.id = asset.id;
        String name = asset.name;
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        item.name = name;
        
        return item;
    }

	@Override
	public void created(StorageAsset asset) {
		MangaItem item = this.toMangaItem(asset);
		if(item == null) {
			return;
		}
		
		String name = item.getMangaName();
		Manga manga = this.mangas.get(name);
		if(manga == null) {
			manga = new Manga();
			manga.id = item.id;
			manga.name = name;
			this.mangas.put(name, manga);
		}
		
		if(!manga.items.contains(item)) {
			manga.items.add(item);
		}
	}

	@Override
	public void deleted(StorageAsset asset) {
		MangaItem item = this.toMangaItem(asset);
		if(item == null) {
			return;
		}
		
		String name = item.getMangaName();
		Manga manga = this.mangas.get(name);
		if(manga != null) {
			manga.items.remove(item);
		}
		if(manga.items.isEmpty()) {
			this.mangas.remove(manga.name);
		}
	}

	@Override
	public void reloaded(List<StorageAsset> assets) {
		this.mangas.clear();
		
		for(StorageAsset asset : assets) {
			created(asset);
		}
	}
	
	public String getImageUrl(MangaItem item) throws Exception {
		if(item == null) {
			return null;
		}
		
		StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
		if(asset == null) {
			return null;
		}
		
		return StorageManager.getInstance().getImageUrl(asset);
	}
	
	public List<String> getImageUrls(Manga manga) throws Exception {
		if(manga == null) {
			return Collections.emptyList();
		}
		
		List<String> urls = new ArrayList<String>();
		for(MangaItem item : manga.items) {
			String url = this.getImageUrl(item);
			if(StringUtils.isNotBlank(url)) {
				urls.add(url);
			}
		}
		
		return urls;
	}
	
	public void deleteManaga(int id) {
		Manga manga = this.getManga(id);
		if(manga == null) {
			return;
		}
		
		final List<String> assetNames = new ArrayList<String>();
		for(MangaItem item : manga.items) {
			StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
			if(asset != null) {
				assetNames.add(asset.name);
			}
		}
		
		ThreadPoolManager.getInstance().execute(new Runnable(){

			@Override
			public void run() {
				for(String assetName : assetNames) {
					try {
						StorageManager.getInstance().deleteStorageAsset(assetName);
					}
					catch(Exception e) {
						logger.error(e);
					}
				}
			}
			
		});
	}
	
	public List<MangaCreator> getMangaCreators() {
		return new ArrayList<MangaCreator>(this.creators.values());
	}
	
	public MangaCreator getMangaCreator(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.creators.get(name);
	}

	@Override
	public Class<MangaCreator> getExtensionClass() {
		return MangaCreator.class;
	}

	@Override
	public void created(MangaCreator t) {
		this.addMangaCreator(t);
	}

	@Override
	public void removed(MangaCreator t) {
		this.removeMangaCreator(t);
	}
	
	public String createManga(String creator, final String parameters) {
		if(StringUtils.isBlank(creator) || StringUtils.isBlank(parameters)) {
			return "Creator and parameters are needed.";
		}
		
		final MangaCreator mangaCreator = this.getMangaCreator(creator);
		if(mangaCreator == null) {
			return "No such manga creator could be found.";
		}
		
		ThreadPoolManager.getInstance().execute(new Runnable(){

			@Override
			public void run() {
				mangaCreator.create(parameters);
			}
			
		});
		
		return null;
	}
}
