package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.model.StatsItem;

public class StatsItemDAO extends AbstractDAO<StatsItem> {
    public static final String TABLE_NAME = "stats";
    
    private static final Logger logger = Logger.getLogger(StatsItemDAO.class);
    
    public StatsItemDAO() {
        this.addQueryTemplate(new StatsItemQueryAllTemplate());
    }
    
    @Override
    public void create(StatsItem item) {
        if(item == null) {
            return;
        }
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into stats(type, time) values (?, ?);";
            ps = con.prepareStatement(sql);
            ps.setString(1, item.type);
            ps.setLong(2, item.time);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to create stats item", e);
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public void update(StatsItem t) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public StatsItem get(int id) {
        return null;
    }

    @Override
    public List<StatsItem> getAll() {
        return Collections.emptyList();
    }

    @Override
    public List<StatsItem> query(QueryTemplate<StatsItem> template, Object... args) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<StatsItem> ret = new ArrayList<StatsItem>();
        try {
            con = DBUtils.getConnection();
            ps = con.prepareStatement(template.getSQL());
            if(template.getQueryHelper() != null) {
                template.getQueryHelper().configurePreparedStatement(ps, args);
            }
            rs = ps.executeQuery();
            while(rs.next()) {
                StatsItem item = new StatsItem();
                item.id = rs.getInt(1);
                item.type = rs.getString(2);
                item.time = rs.getLong(3);
                ret.add(item);
            }
        }
        catch(Exception e) {
            logger.error("Failed to query", e);
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
        return ret;
    }

    @Override
    public String getItemTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getId(StatsItem item) {
        return item.id;
    }

    public static class StatsItemQueryAllTemplate implements QueryTemplate<StatsItem> {
        public static final String NAME = "stats_item_query_all";
        
        private QueryHelper helper = new QueryHelper() {

            @Override
            public void configurePreparedStatement(PreparedStatement ps, Object... args) throws SQLException {
                long last = (Long) args[0];
                long current = (Long) args[1];
                ps.setLong(1, last);
                ps.setLong(2, current);
            }
            
        };
        
        @Override
        public String getID() {
            return NAME;
        }

        @Override
        public String getSQL() {
            return "select * from stats where time > ? and time < ?;";
        }

        @Override
        public QueryHelper getQueryHelper() {
            return helper;
        }

        @Override
        public boolean accept(StatsItem t, Object... args) {
            long last = (Long) args[0];
            long current = (Long) args[1];
            return t.time > last && t.time < current;
        }
    }

    @Override
    public StringBuffer exportSingle(StatsItem t) {
        StringBuffer sb = new StringBuffer("INSERT INTO stats (id, type, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }
}
