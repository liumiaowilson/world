package org.wilson.world.manager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.algorithm.AlgorithmData;
import org.wilson.world.algorithm.RunInfo;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.java.RunJavaInfo;
import org.wilson.world.model.Algorithm;
import org.wilson.world.model.AlgorithmProblem;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class AlgorithmManager implements ItemTypeProvider {
    public static final String NAME = "algorithms";
    
    private static AlgorithmManager instance;
    
    private DAO<Algorithm> dao = null;
    
    @SuppressWarnings("unchecked")
    private AlgorithmManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Algorithm.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Algorithm algorithm : getAlgorithms()) {
                    boolean found = algorithm.name.contains(text) || algorithm.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = algorithm.id;
                        content.name = algorithm.name;
                        content.description = algorithm.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AlgorithmManager getInstance() {
        if(instance == null) {
            instance = new AlgorithmManager();
        }
        return instance;
    }
    
    public void createAlgorithm(Algorithm algorithm) {
        ItemManager.getInstance().checkDuplicate(algorithm);
        
        this.dao.create(algorithm);
    }
    
    public Algorithm getAlgorithm(int id) {
        Algorithm algorithm = this.dao.get(id);
        if(algorithm != null) {
            return algorithm;
        }
        else {
            return null;
        }
    }
    
    public List<Algorithm> getAlgorithms() {
        List<Algorithm> result = new ArrayList<Algorithm>();
        for(Algorithm algorithm : this.dao.getAll()) {
            result.add(algorithm);
        }
        return result;
    }
    
    public void updateAlgorithm(Algorithm algorithm) {
        this.dao.update(algorithm);
    }
    
    public void deleteAlgorithm(int id) {
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
        return target instanceof Algorithm;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Algorithm algorithm = (Algorithm)target;
        return String.valueOf(algorithm.id);
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
        
        Algorithm algorithm = (Algorithm)target;
        return algorithm.name;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RunInfo run(int problemId, String source) throws Exception {
        RunInfo ret = new RunInfo(); 
        if(StringUtils.isBlank(source)) {
            ret.isSuccessful = false;
            ret.message = "Source code should be provided";
            return ret;
        }
        
        AlgorithmProblem problem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(problemId);
        if(problem == null) {
            ret.isSuccessful = false;
            ret.message = "Algorithm problem could not be found";
            return ret;
        }
        
        RunJavaInfo info = JavaManager.getInstance().compile(problem.interfaceDef);
        if(!info.isSuccessful) {
            ret.isSuccessful = false;
            ret.message = info.getMessage();
            return ret;
        }
        
        Class clazz = JavaManager.getInstance().loadClass(info.className);
        Method [] methods = clazz.getDeclaredMethods();
        if(methods.length == 0) {
            ret.isSuccessful = false;
            ret.message = "Cannot find available methods in class [" + info.className + "]";
            return ret;
        }
        
        Method itfMethod = methods[0];
        
        List<AlgorithmData> dataset = AlgorithmProblemManager.getInstance().getDataset(problem.dataset);
        if(dataset.isEmpty()) {
            ret.isSuccessful = false;
            ret.message = "Cannot find available data set";
            return ret;
        }
        int n = DiceManager.getInstance().random(dataset.size());
        AlgorithmData data = dataset.get(n);
        
        info = JavaManager.getInstance().compile(source);
        if(!info.isSuccessful) {
            ret.isSuccessful = false;
            ret.message = info.getMessage();
            return ret;
        }
        
        clazz = JavaManager.getInstance().loadClass(info.className);
        Method implMethod = clazz.getDeclaredMethod(itfMethod.getName(), itfMethod.getParameterTypes());
        
        PrintStream oldOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(out);
        try {
            System.setOut(newOut);
            
            List input = data.input;
            List output = data.output;
            
            if(Modifier.isStatic(implMethod.getModifiers())) {
                implMethod.invoke(null, input.toArray());
            }
            else {
                Object impl = clazz.newInstance();
                implMethod.invoke(impl, input.toArray());
            }
            
            ret.real = input.toString();
            ret.expected = output.toString();
            if(input.equals(output)) {
                ret.isSuccessful = true;
            }
            else {
                ret.isSuccessful = false;
            }
            
            newOut.flush();
            newOut.close();
        }
        finally {
            System.setOut(oldOut);
        }
        
        String log = out.toString();
        ret.message = log;
        
        return ret;
    }
}
