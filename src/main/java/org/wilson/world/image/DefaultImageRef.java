package org.wilson.world.image;

import org.apache.commons.lang.StringUtils;

public class DefaultImageRef implements ImageRef {
	private String name;
	private String url;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl(String url, int width, int height, boolean adjust) {
		if(StringUtils.isBlank(url)) {
			url = this.getUrl();
		}
		
		if(url != null) {
			return url + "&width=" + width + "&height=" + height + "&adjust=" + adjust;
		}
		else {
			return null;
		}
	}
}
