/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Database.Database;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@ManagedBean
@SessionScoped
public class AdminController {
    
    private ArrayList<User> unapprovedUsers;
    private User selectedUser;
    

    /**
     * @return the unapprovedUsers
     */
    public ArrayList<User> getUnapprovedUsers() {
        unapprovedUsers = new ArrayList<User>();
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM useraccount WHERE accountapproval=false");
            ResultSet result = ps.executeQuery();
            
            while (result.next()) {
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
               unapprovedUsers.add(u);
            } 

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return unapprovedUsers;
    }
    
    public void approve()
    {
        selectedUser.approveAccount();
    }
    
    public void deny()
    {
        
    }
    
    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
    
    
}
