/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Database.Database;
import Model.Subscribers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Tim
 */

@ManagedBean
@RequestScoped
public class SubscribeController {
    
    private String keywords;
    private String email;

    private ArrayList<String> keywordList;
    
    public String addSubscriptions()
    {
        addKeywordList();
        int id;
        System.out.print(email);
        for(int i = 0; i < keywordList.size(); i++)
        {
            id = findKeywordID(keywordList.get(i));
            if(id == -1)
            {
                id = createKeyword(keywordList.get(i));
                addSubscriptionToDB(id);
            }
            else
            {
                addSubscriptionToDB(id);
            }
        }
        return "./index.xhtml";
    }
    
    public String removeSubscriptions()
    {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Subscribers WHERE email=?");
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return "./index.xhtml";
    }
    
    private void addSubscriptionToDB(int id)
    {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Subscribers VALUES(?, ?)");
            ps.setInt(1, id);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    private int findKeywordID(String key) {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Keyword WHERE keyword = ?");
            ps.setString(1, key);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return -1;
    }

    private int createKeyword(String key) {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Keyword VALUES(default, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, key);
            if (ps.executeUpdate() == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return -1;
    }
    
    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     */
    public void addKeywordList() {
        String[] ks = getKeywords().split("\\s+");
        this.keywordList = new ArrayList<>(Arrays.asList(ks));
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
}
