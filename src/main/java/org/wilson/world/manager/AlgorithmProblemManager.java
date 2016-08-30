package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.algorithm.AlgorithmData;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.AlgorithmProblem;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.IOUtils;
import org.wilson.world.util.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AlgorithmProblemManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(AlgorithmProblemManager.class);
    
    public static final String NAME = "algorithm_problem";
    
    private static AlgorithmProblemManager instance;
    
    private DAO<AlgorithmProblem> dao = null;
    
    private String defaultInterface = null;
    private String defaultDataset = null;
    
    @SuppressWarnings("unchecked")
    private AlgorithmProblemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(AlgorithmProblem.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(AlgorithmProblem problem : getAlgorithmProblems()) {
                    boolean found = problem.name.contains(text) || problem.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = problem.id;
                        content.name = problem.name;
                        content.description = problem.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AlgorithmProblemManager getInstance() {
        if(instance == null) {
            instance = new AlgorithmProblemManager();
        }
        return instance;
    }
    
    public void createAlgorithmProblem(AlgorithmProblem problem) {
        ItemManager.getInstance().checkDuplicate(problem);
        
        this.dao.create(problem);
    }
    
    public AlgorithmProblem getAlgorithmProblem(int id) {
        AlgorithmProblem problem = this.dao.get(id);
        if(problem != null) {
            return problem;
        }
        else {
            return null;
        }
    }
    
    public List<AlgorithmProblem> getAlgorithmProblems() {
        List<AlgorithmProblem> result = new ArrayList<AlgorithmProblem>();
        for(AlgorithmProblem problem : this.dao.getAll()) {
            result.add(problem);
        }
        return result;
    }
    
    public void updateAlgorithmProblem(AlgorithmProblem problem) {
        this.dao.update(problem);
    }
    
    public void deleteAlgorithmProblem(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof AlgorithmProblem;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        AlgorithmProblem problem = (AlgorithmProblem)target;
        return String.valueOf(problem.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        AlgorithmProblem problem = (AlgorithmProblem)target;
        return problem.name;
    }
    
    public String getDefaultAlgorithmInterface() throws IOException {
        if(this.defaultInterface == null) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("AlgorithmInterface.java");
            this.defaultInterface = IOUtils.toString(is);
        }
        return this.defaultInterface;
    }
    
    public String getDefaultAlgorithmDataset() throws IOException {
        if(this.defaultDataset == null) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("algorithm_dataset.json");
            this.defaultDataset = IOUtils.toString(is);
        }
        return this.defaultDataset;
    }
    
    public List<AlgorithmData> getDataset(String dataset) {
        if(StringUtils.isBlank(dataset)) {
            return Collections.emptyList();
        }
        
        List<AlgorithmData> ret = new ArrayList<AlgorithmData>();
        
        try {
            JSONArray array = JSONArray.fromObject(dataset);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Object inputObj = obj.get("input");
                Object outputObj = obj.get("output");
                
                AlgorithmData data = new AlgorithmData();
                data.input = JSONUtils.convert(inputObj);
                data.output = JSONUtils.convert(outputObj);
                ret.add(data);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        
        return ret;
    }
}
