package org.wilson.world.image;

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

}