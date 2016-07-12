package org.wilson.world.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryHelper {
    public void configurePreparedStatement(PreparedStatement ps, Object... args) throws SQLException;
}
