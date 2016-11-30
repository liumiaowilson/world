package org.wilson.world.idea;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.manager.ExtManager;

public class IdeaConverterFactory implements JavaExtensionListener<IdeaConverter> {
    private static IdeaConverterFactory instance;
    
    private List<IdeaConverter> converters = new ArrayList<IdeaConverter>();
    
    private IdeaConverterFactory() {
        ExtManager.getInstance().addJavaExtensionListener(this);
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

	@Override
	public Class<IdeaConverter> getExtensionClass() {
		return IdeaConverter.class;
	}

	@Override
	public void created(IdeaConverter t) {
		this.addIdeaConverter(t);
	}

	@Override
	public void removed(IdeaConverter t) {
		this.removeIdeaConverter(t);
	}
}
