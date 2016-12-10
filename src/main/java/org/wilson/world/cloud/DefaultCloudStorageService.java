package org.wilson.world.cloud;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.wilson.world.exception.DataException;
import org.wilson.world.manager.WebManager;
import org.wilson.world.util.FormatUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DefaultCloudStorageService implements CloudStorageService {
	private static final Logger logger = Logger.getLogger(DefaultCloudStorageService.class);
	
	private String apiUrl = "https://api.pcloud.com/";
	private String username;
	private String password;
	
	//Cloud storage asset include files and directories
	private Map<String, CloudStorageAsset> assets = new HashMap<String, CloudStorageAsset>();
	private Map<String, CloudStorageAsset> namedAssets = new HashMap<String, CloudStorageAsset>();
	private CloudStorageAsset rootAsset = null;

	@Override
	public void init(Map<String, String> parameters) {
		if(parameters != null) {
			if(parameters.containsKey("url")) {
				apiUrl = parameters.get("url");
				if(!apiUrl.endsWith("/")) {
					apiUrl = apiUrl + "/";
				}
			}
			
			if(parameters.containsKey("username")) {
				username = parameters.get("username");
				if(StringUtils.isBlank(username)) {
					throw new DataException("Username is required.");
				}
			}
			
			if(parameters.containsKey("password")) {
				password = parameters.get("password");
				if(StringUtils.isBlank(password)) {
					throw new DataException("Password is required.");
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "pCloud";
	}

	@Override
	public CloudStorageAsset createCloudStorageAsset(String path, InputStream is) throws Exception {
		if(StringUtils.isBlank(path) || is == null) {
			return null;
		}
		
		String dir = this.getPathDirectory(path);
		String name = this.getPathName(path);
		CloudStorageAsset parent = this.getDirectory(dir);
		if(parent == null) {
			boolean success = this.createDirectory(dir);
			if(!success) {
				throw new DataException("Failed to create directory");
			}
		}
		
		String args = "path=" + dir + "&filename=" + name;
		String url = this.getApiUrl("uploadfile", args);
		
		String content = Jsoup.connect(url).data("file", name, is).ignoreContentType(true).method(Method.POST).execute().body();
		JSONObject obj = JSONObject.fromObject(content);
		JSONArray metadatas = obj.getJSONArray("metadata");
		if(metadatas.isEmpty()) {
			return null;
		}
		JSONObject metadata = metadatas.getJSONObject(0);
		String id = metadata.getString("id");
		CloudStorageAsset asset = CloudStorageAsset.newFileAsset(id, path);
		this.updateFileAsset(asset, metadata);
		this.addCloudStorageAsset(asset);
		
		return asset;
	}

	@Override
	public CloudStorageAsset updateCloudStorageAsset(String path, InputStream is) throws Exception {
		//renaming not supported
		return this.createCloudStorageAsset(path, is);
	}

	@Override
	public CloudStorageAsset getCloudStorageAssetById(String id) throws Exception {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		return this.assets.get(id);
	}

	@Override
	public CloudStorageAsset getCloudStorageAssetByName(String name) throws Exception {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.namedAssets.get(name);
	}

	@Override
	public List<CloudStorageAsset> getCloudStorageAssets() throws Exception {
		return new ArrayList<CloudStorageAsset>(this.assets.values());
	}

	@Override
	public boolean deleteCloudStorageAsset(String id) throws Exception {
		if(StringUtils.isBlank(id)) {
			return false;
		}
		
		CloudStorageAsset asset = this.assets.get(id);
		if(asset == null) {
			return false;
		}
		
		String args = "path=" + asset.name;
		String url = this.getApiUrl("deletefile", args);
		String content = this.getContent(url);
		JSONObject obj = JSONObject.fromObject(content);
		JSONObject metadata = obj.getJSONObject("metadata");
		if(metadata.containsKey("isdeleted") && metadata.getBoolean("isdeleted")) {
			return true;
		}
		
		return false;
	}

	@Override
	public String getPublicUrl(CloudStorageAsset asset) throws Exception {
		if(asset == null) {
			return null;
		}
		
		String code = this.getCode(asset);
		String link = "https://my.pcloud.com/publink/show?code=" + code;
		
		return link;
	}
	
	private String getCode(CloudStorageAsset asset) throws Exception {
		if(asset == null) {
			return null;
		}
		
		String url = this.getApiUrl("getfilepublink", "path=" + asset.name);
		String content = this.getContent(url);
		
		JSONObject obj = JSONObject.fromObject(content);
		String code = obj.getString("code");
		
		return code;
	}
	
	private String getApiUrl(String command, String args) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.apiUrl);
		sb.append(command);
		sb.append("?");
		sb.append("username=").append(this.username);
		sb.append("&");
		sb.append("password=").append(this.password);
		if(args != null) {
			sb.append("&");
			sb.append(args);
		}
		
		return sb.toString();
	}
	
	@Override
	public void sync() throws Exception {
		logger.debug("Sync pcloud...");
		
		String url = this.getApiUrl("listfolder", "path=/&recursive=1");
		String content = this.getContent(url);
		
		loadAssets(content);
	}
	
	private void loadAssets(String content) {
		if(StringUtils.isBlank(content)) {
			return;
		}
		
		this.assets.clear();
		this.namedAssets.clear();
		
		JSONObject contentObj = JSONObject.fromObject(content);
		JSONObject rootObj = contentObj.getJSONObject("metadata");
		if(rootObj != null) {
			this.loadAsset(rootObj, null);
		}
	}
	
	private void updateFileAsset(CloudStorageAsset asset, JSONObject obj) {
		asset.metadata.put("fileid", obj.getString("fileid"));
		asset.metadata.put("isfolder", "false");
		
		//load metadata
		if(obj.containsKey("width")) {
			asset.metadata.put("width", obj.getString("width"));
		}
		if(obj.containsKey("height")) {
			asset.metadata.put("height", obj.getString("height"));
		}
	}
	
	private void updateDirectoryAsset(CloudStorageAsset asset, JSONObject obj) {
		asset.metadata.put("folderid", obj.getString("folderid"));
		asset.metadata.put("isfolder", "true");
	}
	
	public void addCloudStorageAsset(CloudStorageAsset asset) {
		if(asset != null) {
			this.assets.put(asset.id, asset);
			this.namedAssets.put(asset.name, asset);
		}
	}
	
	public void removeCloudStorageAsset(CloudStorageAsset asset) {
		if(asset != null) {
			this.assets.remove(asset.id);
			this.namedAssets.remove(asset.name);
		}
	}
	
	private void loadAsset(JSONObject obj, String contextPath) {
		if(obj == null) {
			return;
		}
		
		if(contextPath == null) {
			contextPath = "";
		}
		
		if(obj.containsKey("isfolder") && obj.getBoolean("isfolder")) {
			//folder
			String name = obj.getString("name");
			JSONArray contents = obj.getJSONArray("contents");
			if(!"/".equals(name)) {
				contextPath = contextPath + "/" + name;
				String id = obj.getString("id");
				
				CloudStorageAsset asset = CloudStorageAsset.newDirectoryAsset(id, contextPath);
				this.updateDirectoryAsset(asset, obj);
				
				this.addCloudStorageAsset(asset);
			}
			else {
				String id = obj.getString("id");
				this.rootAsset = CloudStorageAsset.newDirectoryAsset(id, "/");
				this.updateDirectoryAsset(this.rootAsset, obj);
			}
			
			for(int i = 0; i < contents.size(); i++) {
				JSONObject childObj = contents.getJSONObject(i);
				this.loadAsset(childObj, contextPath);
			}
		}
		else {
			//file
			String name = obj.getString("name");
			String id = obj.getString("id");
			CloudStorageAsset asset = CloudStorageAsset.newFileAsset(id, contextPath + "/" + name);
			this.updateFileAsset(asset, obj);
			
			this.addCloudStorageAsset(asset);
		}
	}
	
	private String getContent(String url) throws Exception {
		return WebManager.getInstance().getContent(url);
	}

	@Override
	public double getUsedPercentage() throws Exception {
		String url = this.getApiUrl("userinfo", null);
		String content = this.getContent(url);
		
		JSONObject obj = JSONObject.fromObject(content);
		int quota = obj.getInt("quota");
		int usedQuota = obj.getInt("usedquota");
		
		return FormatUtils.getRoundedValue(usedQuota * 100.0 / quota);
	}

	@Override
	public String getImageUrl(CloudStorageAsset asset, int width, int height, boolean adjust) throws Exception {
		if(asset == null) {
			return null;
		}
		
		String code = this.getCode(asset);
		String url = "https://api.pcloud.com/getpubthumb?code=" + code;
		
		if(width < 0) {
			width = asset.getInt("width");
		}
		if(height < 0) {
			height = asset.getInt("height");
		}
		
		return url + "&linkpassword=undefined&size=" + width + "x" + height + "&crop=" + (adjust ? "0" : "1") + "&type=auto";
	}
	
	public CloudStorageAsset getDirectory(String name) {
		if(StringUtils.isBlank(name)) {
			return this.rootAsset;
		}
		
		for(CloudStorageAsset asset : this.assets.values()) {
			if(asset.isDirectory()) {
				if(name.equals(asset.name)) {
					return asset;
				}
			}
		}
		
		return null;
	}
	
	private String getPathDirectory(String path) {
		int pos = path.lastIndexOf("/");
		if(pos >= 0) {
			return path.substring(0, pos);
		}
		else {
			return null;
		}
	}
	
	private String getPathName(String path) {
		int pos = path.lastIndexOf("/");
		if(pos >= 0) {
			return path.substring(pos + 1);
		}
		else {
			return null;
		}
	}
	
	public boolean createDirectory(String dir) throws Exception {
		if(StringUtils.isBlank(dir)) {
			return false;
		}
		
		String parent = this.getPathDirectory(dir);
		CloudStorageAsset parentAsset = this.getDirectory(parent);
		if(parentAsset == null) {
			boolean success = this.createDirectory(parent);
			if(!success) {
				return false;
			}
		}
		
		String name = this.getPathName(dir);
		if(StringUtils.isBlank(name)) {
			return false;
		}
		
		String args = "path=" + dir;
		
		String url = this.getApiUrl("createfolder", args);
		String content = this.getContent(url);
		
		JSONObject obj = JSONObject.fromObject(content);
		JSONObject metadata = obj.getJSONObject("metadata");
		if(metadata == null) {
			return false;
		}
		
		if(!metadata.containsKey("id")) {
			return false;
		}
		
		String id = metadata.getString("id");
		CloudStorageAsset asset = CloudStorageAsset.newDirectoryAsset(id, dir);
		asset.metadata.put("folderid", metadata.getString("folderid"));
		this.addCloudStorageAsset(asset);
		
		return true;
	}

	@Override
	public String getSampleConfigData() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("    \"username\":\"xxx\",\n");
		sb.append("    \"password\":\"xxx\"\n");
		sb.append("}");
		
		return sb.toString();
	}

	@Override
	public String getServiceUrl() {
		return "https://my.pcloud.com/";
	}

	@Override
	public String getContent(CloudStorageAsset asset) throws Exception {
		if(asset == null) {
			return null;
		}
		
		String args = "path=" + asset.name;
		String url = this.getApiUrl("gettextfile", args);
		String content = this.getContent(url);
		
		return content;
	}

	@Override
	public boolean hasPath(String path) {
		if(StringUtils.isBlank(path)) {
			return false;
		}
		
		return this.namedAssets.get(path) != null;
	}
}
