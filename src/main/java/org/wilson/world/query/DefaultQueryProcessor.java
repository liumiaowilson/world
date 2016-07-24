package org.wilson.world.query;

import java.util.Collections;
import java.util.List;

import org.wilson.world.model.Query;
import org.wilson.world.model.QueryItem;

public class DefaultQueryProcessor implements QueryProcessor {
    private Query query;
    private QueryHandler handler;
    
    public DefaultQueryProcessor(Query query, QueryHandler handler) {
        this.query = query;
        this.handler = handler;
    }
    
    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public QueryHandler getHandler() {
        return handler;
    }

    public void setHandler(QueryHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() {
        return this.query.name;
    }

    @Override
    public List<QueryItem> query() {
        return this.handler.doQuery();
    }

    @Override
    public String getIDCellExpression() {
        return this.query.idExpr;
    }

    @Override
    public String getNameCellExpression() {
        return this.query.nameExpr;
    }

    @Override
    public List<QueryButtonConfig> getQueryButtonConfigs() {
        return Collections.emptyList();
    }

    @Override
    public int getID() {
        return this.query.id;
    }

    @Override
    public boolean isQuickLink() {
        return true;
    }

}
