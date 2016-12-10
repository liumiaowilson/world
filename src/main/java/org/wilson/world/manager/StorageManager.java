package org.wilson.world.manager;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.chart.PieChartData;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.model.Storage;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.storage.StorageProvider;
import org.wilson.world.storage.StorageStatus;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.NullWebJobMonitor;
import org.wilson.world.web.WebJobMonitor;

public class StorageManager implements ItemTypeProvider, JavaExtensionListener<StorageProvider> {
    private static final Logger logger = Logger.getLogger(StorageManager.class);
    
    public static final String NAME = "storage";
    
    private static StorageManager instance;
    
    private DAO<Storage> dao = null;
    
    private Map<String, StorageAsset> assets = new HashMap<String, StorageAsset>();
    private Map<Integer, StorageAsset> ids = new HashMap<Integer, StorageAsset>();
    
    private static int GLOBAL_ID = 1;
    
    private List<StorageListener> listeners = new ArrayList<StorageListener>();
    
    private Map<Integer, StorageStatus> statuses = new HashMap<Integer, StorageStatus>();
    
    private Map<String, StorageProvider> providers = new HashMap<String, StorageProvider>();
    
    @SuppressWarnings("unchecked")
    private StorageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Storage.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Storage storage : getStorages()) {
                    boolean found = storage.name.contains(text) || storage.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = storage.id;
                        content.name = storage.name;
                        content.description = storage.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        ExtManager.getInstance().addJavaExtensionListener(this);
    }
    
    public static StorageManager getInstance() {
        if(instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }
    
    public void addStorageProvider(StorageProvider provider) {
    	if(provider != null) {
    		this.providers.put(provider.getName(), provider);
    	}
    }
    
    public void removeStorageProvider(StorageProvider provider) {
    	if(provider != null) {
    		this.providers.remove(provider.getName());
    	}
    }
    
    public void createStorage(Storage storage) {
        ItemManager.getInstance().checkDuplicate(storage);
        
        this.dao.create(storage);
    }
    
    public Storage getStorage(int id) {
        Storage storage = this.dao.get(id);
        if(storage != null) {
        	storage = this.loadStorage(storage);
            return storage;
        }
        else {
            return null;
        }
    }
    
    private Storage loadStorage(Storage storage) {
    	if(storage == null) {
    		return null;
    	}
    	
    	storage.status = this.getStorageStatus(storage);
    	
    	return storage;
    }
    
    public List<Storage> getStorages() {
        List<Storage> result = new ArrayList<Storage>();
        for(Storage storage : this.dao.getAll()) {
        	storage = this.loadStorage(storage);
            result.add(storage);
        }
        return result;
    }
    
    public void updateStorage(Storage storage) {
        this.dao.update(storage);
    }
    
    public void deleteStorage(int id) {
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
        return target instanceof Storage;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Storage storage = (Storage)target;
        return String.valueOf(storage.id);
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
        
        Storage storage = (Storage)target;
        return storage.name;
    }
    
    public void addStorageAsset(StorageAsset asset) {
        if(asset == null) {
            return;
        }
        this.assets.put(asset.name, asset);
        this.ids.put(asset.id, asset);
    }
    
    public void removeStorageAsset(StorageAsset asset) {
        if(asset == null) {
            return;
        }
        this.assets.remove(asset.name);
        this.ids.remove(asset.id);
    }
    
    public void sync() throws Exception {
        this.sync(null);
    }
    
    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }
    
    private String getChecksum(Storage storage, StorageAsset asset) throws Exception {
    	if(!this.isChecksumEnabled()) {
    		return null;
    	}
    	
        InputStream is = null;
        try {
            URL urlObj = new URL(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=get&path=" + encode(asset.name));
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.connect();
            
            is = connection.getInputStream();
            
            return IOUtils.getChecksum(is);
        }
        finally {
            if(is != null) {
                is.close();
            }
        }
    }
    
    private boolean isChecksumEnabled() {
    	return ConfigManager.getInstance().getConfigAsBoolean("storage.checksum.enabled", false);
    }
    
    public void sync(WebJobMonitor monitor) throws Exception {
        if(monitor == null) {
            monitor = new NullWebJobMonitor();
        }
        
        monitor.start(this.getStorages().size());
        
        GLOBAL_ID = 1;
        this.assets.clear();
        this.ids.clear();
        
        for(Storage storage : this.getStorages()) {
        	try {
        		String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=list");
                if(!StringUtils.isBlank(resp)) {
                    resp = resp.trim();
                    if(resp.startsWith("[ERROR]")) {
                        logger.warn(resp);
                        continue;
                    }
                    
                    String [] lines = resp.split("\n");
                    monitor.adjust(lines.length);
                    for(String line : lines) {
                        if(!StringUtils.isBlank(line)) {
                            line = line.trim();
                            StorageAsset asset = new StorageAsset();
                            asset.id = GLOBAL_ID++;
                            asset.name = line;
                            asset.storageId = storage.id;
                            asset.checksum = this.getChecksum(storage, asset);
                            this.addStorageAsset(asset);
                        }
                        
                        if(monitor.isStopRequired()) {
                            monitor.stop();
                            break;
                        }
                        monitor.progress(1);
                    }
                }
                
                if(monitor.isStopRequired()) {
                    monitor.stop();
                    break;
                }
                monitor.progress(1);
        	}
        	catch(Exception e) {
        		logger.error(e);
        	}
        }
        
        //load from storage providers
        for(StorageProvider provider : this.providers.values()) {
        	try {
        		int count = provider.sync(GLOBAL_ID);
            	GLOBAL_ID += count + 1;
        	}
        	catch(Exception e) {
        		logger.error(e);
        	}
        }
        
        List<StorageAsset> assets = this.getStorageAssets();
        for(StorageListener listener : listeners) {
            listener.reloaded(assets);
        }
    }
    
	public List<StorageAsset> getStorageAssets() {
    	List<StorageAsset> assets = new ArrayList<StorageAsset>();
    	
    	//load local storage assets
    	assets.addAll(this.assets.values());
    	
    	//load from providers
    	for(StorageProvider provider : this.providers.values()) {
    		assets.addAll(provider.getStorageAssets().values());
    	}
    	
    	return assets;
    }
    
    public StorageAsset getStorageAsset(int id) {
    	for(StorageProvider provider : this.providers.values()) {
    		StorageAsset asset = provider.getStorageAsset(id);
    		if(asset != null) {
    			return asset;
    		}
    	}
    	
        return this.ids.get(id);
    }
    
    public StorageAsset getStorageAsset(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(StorageProvider provider : this.providers.values()) {
        	StorageAsset asset = provider.getStorageAsset(name);
        	if(asset != null) {
        		return asset;
        	}
        }
        
        return this.assets.get(name);
    }
    
    public Storage randomStorage() {
        List<Storage> storages = new ArrayList<Storage>();
        for(Storage storage : this.getStorages()) {
        	if(StorageStatus.Light == storage.status || StorageStatus.Medium == storage.status || StorageStatus.Heavy == storage.status) {
        		storages.add(storage);
        	}
        }
        
        if(storages.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(storages.size());
        return storages.get(n);
    }
    
    public StorageProvider randomStorageProvider() {
    	List<StorageProvider> providers = new ArrayList<StorageProvider>();
    	for(StorageProvider provider : this.providers.values()) {
    		StorageStatus status = provider.getStorageStatus();
    		if(StorageStatus.Light == status || StorageStatus.Medium == status || StorageStatus.Heavy == status) {
    			providers.add(provider);
    		}
    	}
    	
    	if(providers.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(providers.size());
    	return providers.get(n);
    }
    
    public StorageProvider getPreferredStorageProvider(StorageAsset asset) throws Exception {
    	if(asset == null) {
    		return null;
    	}
    	
    	for(StorageProvider provider : this.providers.values()) {
    		if(provider.prefer(asset)) {
    			return provider;
    		}
    	}
    	
    	return null;
    }
    
    public void addStorageListener(StorageListener listener) {
        if(listener != null) {
            this.listeners.add(listener);
        }
    }
    
    public void removeStorageListener(StorageListener listener) {
        if(listener != null) {
            this.listeners.remove(listener);
        }
    }
    
    public String createStorageAsset(String name, String url) throws Exception{
        if(StringUtils.isBlank(name)) {
            return "Storage asset name should be provided";
        }
        if(StringUtils.isBlank(url)) {
            return "Storage asset url should be provided";
        }
        
        StorageAsset asset = this.getStorageAsset(name);
        if(asset != null) {
            return "Storage asset with the same name already exists";
        }
        
        StorageProvider provider = this.getPreferredStorageProvider(asset);
        if(provider == null) {
        	provider = this.randomStorageProvider();
        }
        if(provider != null) {
        	asset = provider.createStorageAsset(name, url, GLOBAL_ID++);
        	if(asset == null) {
        		return "Failed to create storage asset with provider [" + provider.getName() + "].";
        	}
        }
        else {
            Storage storage = this.randomStorage();
            if(storage == null) {
                return "No storage could be found";
            }
            
            String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=create&path=" + encode(name) + "&url=" + encode(url));
            
            if(!StringUtils.isBlank(resp) && resp.trim().startsWith("[ERROR]")) {
                return resp;
            }
            
            asset = new StorageAsset();
            asset.id = GLOBAL_ID++;
            asset.name = name;
            asset.storageId = storage.id;
            asset.checksum = this.getChecksum(storage, asset);
            this.addStorageAsset(asset);
        }
        
        for(StorageListener listener : listeners) {
            listener.created(asset);
        }
        
        return null;
    }
    
    public StorageProvider getStorageProvider(StorageAsset asset) {
    	if(asset == null) {
    		return null;
    	}
    	
    	for(StorageProvider provider : this.providers.values()) {
    		if(provider.getStorageAsset(asset.id) != null) {
    			return provider;
    		}
    	}
    	
    	return null;
    }
    
    public String deleteStorageAsset(String name) throws Exception {
        if(StringUtils.isBlank(name)) {
            return "Storage asset name should be provided";
        }
        
        StorageAsset asset = this.getStorageAsset(name);
        if(asset == null) {
            return "No such storage asset could be found";
        }
        
        StorageProvider provider = this.getStorageProvider(asset);
        if(provider != null) {
        	String msg = provider.deleteStorageAsset(asset);
        	if(msg != null) {
        		return msg;
        	}
        }
        else {
            Storage storage = this.getStorage(asset.storageId);
            
            String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=delete&path=" + encode(name));
            if(!StringUtils.isBlank(resp) && resp.trim().startsWith("[ERROR]")) {
                return resp;
            }
            
            this.removeStorageAsset(asset);
        }
        
        for(StorageListener listener : listeners) {
            listener.deleted(asset);
        }
        
        return null;
    }
    
    public String getImageUrl(StorageAsset asset, int width, int height, boolean adjust) throws Exception {
    	if(asset == null) {
    		return "";
    	}
    	
    	StorageProvider provider = this.getStorageProvider(asset);
    	if(provider != null) {
    		return provider.getImageUrl(asset, width, height, adjust);
    	}
    	
    	String url = this.getImageUrl(asset);
		
		if(url != null) {
			return url + "&width=" + width + "&height=" + height + "&adjust=" + adjust;
		}
		else {
			return null;
		}
    }
    
    public String getImageUrl(StorageAsset asset) throws Exception{
        if(asset == null) {
            return "";
        }
        
        StorageProvider provider = this.getStorageProvider(asset);
        if(provider != null) {
        	return provider.getImageUrl(asset);
        }
        
        Storage storage = this.getStorage(asset.storageId);
        if(storage == null) {
            return "";
        }
        
        return storage.url + "/servlet/image?key=" + encode(storage.key) + "&path=" + encode(asset.name);
    }
    
    public String getDownloadUrl(Storage storage) throws Exception {
    	if(storage == null) {
    		return null;
    	}
    	
    	return storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=download";
    }
    
    public String getContent(StorageAsset asset) throws Exception {
        if(asset == null) {
            return null;
        }
        
        StorageProvider provider = this.getStorageProvider(asset);
        if(provider != null) {
        	String content = provider.getContent(asset);
        	if(StringUtils.isNotBlank(content)) {
        		return content;
        	}
        }
        
        Storage storage = this.getStorage(asset.storageId);
        if(storage == null) {
            return null;
        }
        
        String content = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=get&path=" + encode(asset.name));
        
        return content;
    }
    
    public boolean hasDuplicate(String checksum) {
        for(StorageAsset asset : this.getStorageAssets()) {
            if(asset.checksum != null && asset.checksum.equals(checksum)) {
                return true;
            }
        }
        
        return false;
    }
    
    public int [] getStorageUsage(Storage storage) throws Exception {
    	if(storage == null) {
    		return null;
    	}
    	
    	String content = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + encode(storage.key) + "&command=execute&cmd=" + encode("quota -s"));
    	
    	String [] lines = content.split("\n");
        String last_line = lines[lines.length - 1].trim();
        if(logger.isTraceEnabled()) {
            logger.trace("last line is " + last_line);
        }
        String [] items = last_line.split("\\s+");
        int [] ret = new int[2];
        
        String used = items[0];
        used = used.substring(0, used.length() - 1);
        ret[0] = Integer.parseInt(used);
        
        String max = items[2];
        max = max.substring(0, max.length() - 1);
        ret[1] = Integer.parseInt(max);
        
        return ret;
    }
    
    public PieChartData getStorageUsagePieChartData(Storage storage) throws Exception {
    	if(storage == null) {
    		return null;
    	}
    	
    	int [] usage = this.getStorageUsage(storage);
    	if(usage == null || usage.length != 2) {
    		return null;
    	}
    	
    	Map<String, Double> data = new HashMap<String, Double>();
    	int used = usage[0];
    	int max = usage[1];
    	int free = max - used;
    	double used_pct = FormatUtils.getRoundedValue(used * 100.0 / max);
    	double free_pct = FormatUtils.getRoundedValue(free * 100.0 / max);
    	data.put("Used", used_pct);
    	data.put("Free", free_pct);
    	
    	return new PieChartData("storage_" + storage.id + "_usage", storage.name + " Usage", data);
    }
    
    public PieChartData getStorageUsagePieChartData(StorageProvider provider) throws Exception {
    	if(provider == null) {
    		return null;
    	}
    	
    	double used = provider.getUsedPercentage();
    	double free = 100.0 - used;
    	
    	Map<String, Double> data = new HashMap<String, Double>();
    	data.put("Used", used);
    	data.put("Free", free);
    	
    	return new PieChartData("storage_provider_" + provider.getName(), provider.getName() + " Usage", data);
    }
    
    public List<PieChartData> getStorageUsagePieChartDatas() throws Exception {
    	List<PieChartData> ret = new ArrayList<PieChartData>();
    	
    	for(Storage storage : this.getStorages()) {
    		PieChartData data = this.getStorageUsagePieChartData(storage);
    		if(data != null) {
    			ret.add(data);
    		}
    	}
    	
    	for(StorageProvider provider : this.providers.values()) {
    		PieChartData data = this.getStorageUsagePieChartData(provider);
    		if(data != null) {
    			ret.add(data);
    		}
    	}
    	
    	return ret;
    }
    
    public void setStorageStatus(Storage storage, StorageStatus status) {
    	if(storage == null) {
    		return;
    	}
    	if(status == null) {
    		status = StorageStatus.Unknown;
    	}
    	storage.status = status;
    	this.statuses.put(storage.id, status);
    }
    
    public StorageStatus getStorageStatus(Storage storage) {
    	if(storage == null) {
    		return null;
    	}
    	
    	StorageStatus status = this.statuses.get(storage.id);
    	if(status == null) {
    		status = StorageStatus.Unknown;
    	}
    	
    	return status;
    }

	@Override
	public Class<StorageProvider> getExtensionClass() {
		return StorageProvider.class;
	}

	@Override
	public void created(StorageProvider t) {
		this.addStorageProvider(t);
	}

	@Override
	public void removed(StorageProvider t) {
		this.removeStorageProvider(t);
	}
	
	public StorageStatus toStorageStatus(double used_pct) {
		if(used_pct < 0.3) {
			return StorageStatus.Light;
		}
		else if(used_pct < 0.6) {
			return StorageStatus.Medium;
		}
		else if(used_pct < 0.9) {
			return StorageStatus.Heavy;
		}
		else {
			return StorageStatus.Full;
		}
	}
}
