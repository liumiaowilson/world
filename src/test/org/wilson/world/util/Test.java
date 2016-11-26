package org.wilson.world.util;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;

public class Test {

	public static void main(String[] args) throws IOException {
		Document doc = WebManager.getInstance().parse("http://www.51av.biz/?p=11281");
		Elements elements = doc.select("td#continfo p img");
		for(int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String url = element.attr("src");
			String [] items= url.split(" ");
			url = items[0].trim();
		}
	}

}