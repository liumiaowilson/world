package org.wilson.world.query;

public class QueryButtonConfig {
    public String name;
    
    public String url;
    
    public static QueryButtonConfig create(String name, String url) {
        QueryButtonConfig config = new QueryButtonConfig();
        config.name = name;
        config.url = url;
        return config;
    }
}
