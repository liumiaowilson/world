package org.wilson.world.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;

@Path("/demo")
public class DemoAPI {
    private Logger logger = Logger.getLogger(DemoAPI.class);
    
    @GET
    @Path("/msg")
    public Response message() {
        if(logger.isDebugEnabled()) {
            logger.debug("print demo message");
        }
        String msg = "Hello World";
        
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            String sql = "select * from names;";
            rs = s.executeQuery(sql);
            if(rs.next()) {
                String firstname = rs.getString(2);
                String lastname = rs.getString(3);
                msg = "Hello " + firstname + " " + lastname;
            }
        }
        catch(Exception e) {
            logger.error("failed to query message", e);
        }
        finally {
            DBUtils.closeQuietly(con, s, rs);
        }
        
        return Response.status(200).entity(msg).build();
    }
}
