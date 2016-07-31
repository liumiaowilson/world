package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.UserSkill;

import com.mysql.jdbc.Statement;

public class UserSkillDAO extends AbstractDAO<UserSkill> {
    public static final String ITEM_TABLE_NAME = "user_skills";
    
    private static final Logger logger = Logger.getLogger(UserSkillDAO.class);

    @Override
    public void create(UserSkill skill) {
        if(skill == null) {
            throw new DataException("skill should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into user_skills(skill_id, level, exp, last_time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, skill.skillId);
            ps.setInt(2, skill.level);
            ps.setInt(3, skill.exp);
            ps.setLong(4, skill.lastTime);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                skill.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create skill", e);
            throw new DataException("failed to create skill");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(UserSkill skill) {
        if(skill == null) {
            throw new DataException("skill should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update user_skills set skill_id = ?, level = ?, exp = ?, last_time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, skill.skillId);
            ps.setInt(2, skill.level);
            ps.setInt(3, skill.exp);
            ps.setLong(4, skill.lastTime);
            ps.setInt(5, skill.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update skill", e);
            throw new DataException("failed to update skill");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public void delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from user_skills where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete skill", e);
            throw new DataException("failed to delete skill");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public UserSkill get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from user_skills where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                UserSkill skill = new UserSkill();
                skill.id = id;
                skill.skillId = rs.getInt(2);
                skill.level = rs.getInt(3);
                skill.exp = rs.getInt(4);
                skill.lastTime = rs.getLong(5);
                return skill;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get skill", e);
            throw new DataException("failed to get skill");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<UserSkill> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from user_skills;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<UserSkill> skills = new ArrayList<UserSkill>();
            while(rs.next()) {
                UserSkill skill = new UserSkill();
                skill.id = rs.getInt(1);
                skill.skillId = rs.getInt(2);
                skill.level = rs.getInt(3);
                skill.exp = rs.getInt(4);
                skill.lastTime = rs.getLong(5);
                skills.add(skill);
            }
            return skills;
        }
        catch(Exception e) {
            logger.error("failed to get skills", e);
            throw new DataException("failed to get skills");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<UserSkill> query(QueryTemplate<UserSkill> template, Object... args) {
        return new ArrayList<UserSkill>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(UserSkill skill) {
        return skill.id;
    }

    @Override
    public StringBuffer exportSingle(UserSkill t) {
        StringBuffer sb = new StringBuffer("INSERT INTO user_skills (id, skill_id, level, exp, last_time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.skillId);
        sb.append(",");
        sb.append(t.level);
        sb.append(",");
        sb.append(t.exp);
        sb.append(",");
        sb.append(t.lastTime);
        sb.append(");");
        return sb;
    }

}
