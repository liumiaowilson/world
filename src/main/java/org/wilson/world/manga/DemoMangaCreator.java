package org.wilson.world.manga;

public class DemoMangaCreator implements MangaCreator {

	@Override
	public String getName() {
		return "Demo";
	}

	@Override
	public void create(String parameters) {
		System.out.println("This is a demo manga creator that does nothing but accept parameters [" + parameters + "]");
	}

	@Override
	public String getParametersHint() {
		return "#id #name";
	}

}
