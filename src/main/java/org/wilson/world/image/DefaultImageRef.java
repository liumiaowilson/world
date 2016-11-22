package org.wilson.world.image;

public class DefaultImageRef implements ImageRef {
	private String name;
	private String url;
	private int width;
	private int height;
	private boolean adjust = true;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getUrl() {
		if(this.width >= 0 && this.height >= 0) {
			return this.url + "&width=" + width + "&height=" + height + "&adjust=" + adjust;
		}
		else {
			return this.url;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void setAdjust(boolean adjust) {
		this.adjust = adjust;
	}

	@Override
	public boolean isAdjust() {
		return this.adjust;
	}

}
