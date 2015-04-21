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
import java.util.ArrayList;

/**
 *
 * @author it3530203
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String userID;
    private String password;
    private String email;
    private String securityQuestion;
    private String securityAnswer;
    private String accountReason;
    private String type;
    private boolean accountApproval;
    
    private ArrayList<Project> projects = new ArrayList<>();

    public User() {
        type="user";
    }

    
    public boolean verifyUserID(){
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM UserAccount WHERE userID = ?");
            ps.setString(1, userID);
            ResultSet result = ps.executeQuery();
            if(result.next()){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return true;
    }
    
    public boolean add(){
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO UserAccount VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, 'user', false)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userID);
            ps.setString(4, password);
            ps.setString(5, email);
            ps.setString(6, securityQuestion);
            ps.setString(7, securityAnswer);
            ps.setString(8, accountReason);
            //ps.setString(9, type);
            //ps.setBoolean(10, accountApproval);
            if(ps.executeUpdate() == 1){
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }
    
    public boolean update(){
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE UserAccount SET firstName = ?, lastName = ?, password = ?, email = ?, securityQuestion = ?, securityAnswer = ?, accountReason = ?, accountApproval = ? WHERE userID = ?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, password);
            ps.setString(4, email);
            ps.setString(5, securityQuestion);
            ps.setString(6, securityAnswer);
            ps.setString(7, accountReason);
            ps.setBoolean(8, accountApproval);
            ps.setString(9, userID);
            if(ps.executeUpdate() == 1){
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }
    
    public static User login(String userID, String password){
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM UserAccount WHERE userID = ? AND password = ?  AND accountApproval = True");
            ps.setString(1, userID);
            ps.setString(2, password);
            ResultSet result = ps.executeQuery();
            if(result.next()){
               u = new User();
               u.setId(result.getInt("id"));
               u.setFirstName(result.getString("firstName"));
               u.setLastName(result.getString("lastName"));
               u.setUserID(result.getString("userID"));
               u.setPassword(result.getString("password"));
               u.setEmail(result.getString("email"));
               u.setSecurityQuestion(result.getString("securityQuestion"));
               u.setSecurityAnswer(result.getString("securityAnswer"));
               u.setAccountReason(result.getString("accountReason"));
               u.setType(result.getString("type"));
               u.setAccountApproval(result.getBoolean("accountApproval"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return u;
    }
    
    public boolean approveAccount(){
        accountApproval = true;
        return update();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getAccountReason() {
        return accountReason;
    }

    public void setAccountReason(String accountReason) {
        this.accountReason = accountReason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAccountApproval() {
        return accountApproval;
    }

    public void setAccountApproval(boolean accountApproval) {
        this.accountApproval = accountApproval;
    }
    
   
}
