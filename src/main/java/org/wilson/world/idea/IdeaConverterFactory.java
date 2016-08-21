package org.wilson.world.idea;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class IdeaConverterFactory {
    private static IdeaConverterFactory instance;
    
    private List<IdeaConverter> converters = new ArrayList<IdeaConverter>();
    
    private IdeaConverterFactory() {
        
    }
    
    public static IdeaConverterFactory getInstance() {
        if(instance == null) {
            instance = new IdeaConverterFactory();
        }
        return instance;
    }
    
    public void addIdeaConverter(IdeaConverter converter) {
        if(converter != null) {
            this.converters.add(converter);
        }
    }
    
    public void removeIdeaConverter(IdeaConverter converter) {
        if(converter != null) {
            this.converters.remove(converter);
        }
    }
    
    public List<IdeaConverter> getIdeaConverters() {
        return this.converters;
    }
    
    public IdeaConverter getIdeaConverterByName(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(IdeaConverter converter : this.converters) {
            if(converter.getName().equals(name)) {
                return converter;
            }
        }
        
        return null;
    }
}
