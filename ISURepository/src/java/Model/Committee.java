/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

/**
 *
 * @author it3530203
 */
public class Committee {

    private int id;
    private Project project;
    private String name;
    private String email;
    private CommitteeType type;

    public enum CommitteeType {

        Chair, Member, Advisor
    }

    public Committee() {
    }

    public Committee(String name, String email, CommitteeType type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public boolean add() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Committee VALUES (default, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, project.getId());
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, type.toString());
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
                rs.close();
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CommitteeType getType() {
        return type;
    }

    public void setType(CommitteeType type) {
        this.type = type;
    }

}
