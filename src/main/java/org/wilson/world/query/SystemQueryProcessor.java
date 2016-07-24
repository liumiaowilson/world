package org.wilson.world.query;

import java.util.Collections;
import java.util.List;

public abstract class SystemQueryProcessor implements QueryProcessor {
    @Override
    public boolean isQuickLink() {
        return false;
    }

    private int id;
    
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getIDCellExpression() {
        return "$(nTd).html(\"<a href=\\\"javascript:jumpTo('\" + oData.type + \"_edit.jsp?id=\" + oData.id + \"')\\\">\" + oData.id + \"</a>\");";
    }

    @Override
    public String getNameCellExpression() {
        return "var content = oData.name;\n" + 
               "$(nTd).html(content);\n" + 
               "nTd.title = oData.description;";
    }

    @Override
    public List<QueryButtonConfig> getQueryButtonConfigs() {
        return Collections.emptyList();
    }

}
