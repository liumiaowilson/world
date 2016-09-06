package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Expression;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ExpressionManager implements ItemTypeProvider {
    public static final String NAME = "expression";
    
    private static ExpressionManager instance;
    
    private DAO<Expression> dao = null;
    
    @SuppressWarnings("unchecked")
    private ExpressionManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Expression.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Expression expression : getExpressions()) {
                    boolean found = expression.name.contains(text) || expression.keywords.contains(text) || expression.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = expression.id;
                        content.name = expression.name;
                        content.description = expression.keywords;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ExpressionManager getInstance() {
        if(instance == null) {
            instance = new ExpressionManager();
        }
        return instance;
    }
    
    public void createExpression(Expression expression) {
        ItemManager.getInstance().checkDuplicate(expression);
        
        this.dao.create(expression);
    }
    
    public Expression getExpression(int id) {
        Expression expression = this.dao.get(id);
        if(expression != null) {
            return expression;
        }
        else {
            return null;
        }
    }
    
    public List<Expression> getExpressions() {
        List<Expression> result = new ArrayList<Expression>();
        for(Expression expression : this.dao.getAll()) {
            result.add(expression);
        }
        return result;
    }
    
    public void updateExpression(Expression expression) {
        this.dao.update(expression);
    }
    
    public void deleteExpression(int id) {
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
        return target instanceof Expression;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Expression expression = (Expression)target;
        return String.valueOf(expression.id);
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
        
        Expression expression = (Expression)target;
        return expression.name;
    }
}
