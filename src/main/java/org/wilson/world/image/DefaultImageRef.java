package org.wilson.world.image;

public class DefaultImageRef implements ImageRef {
	private String name;
	private String url;
	private int width;
	private int height;
	private boolean adjust;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getUrl() {
		return this.url + "&width=" + width + "&height=" + height + "&adjust=" + adjust;
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
