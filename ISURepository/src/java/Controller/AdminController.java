/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Database.Database;
import Model.Project;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class AdminController {

    private ArrayList<User> unapprovedUsers;
    private ArrayList<User> approvedUsers;
    private ArrayList<Project> allProjects;
    private ArrayList<Project> highlightedProjects;
    private User selectedUser;
    private int userAmount;

    public AdminController() {
        //approvedUsers = getApprovedUsers();
    }

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

    /**
     * @return the approvedUsers
     */
    public ArrayList<User> getApprovedUsers() {
        approvedUsers = new ArrayList<User>(getUserAmount());
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM useraccount WHERE accountapproval=true");
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
                approvedUsers.add(u);
            }

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return approvedUsers;
    }
    
    public int countApprovedUsers() {
        int count = 0;
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT count(id) as total FROM useraccount WHERE accountapproval=true");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                count = result.getInt("total");
            }

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return count;
    }
    
    public int countTheses() {
        int count = 0;
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT count(id) as total FROM Project");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                count = result.getInt("total");
            }

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return count;
    }
    
    public ArrayList<ArrayList<String>> countProjectCourses() {
        ArrayList<ArrayList<String>> courses = new ArrayList<>();

        Connection conn = Database.connect2DB();
        try {
          PreparedStatement ps = conn.prepareStatement("SELECT COUNT(PROJECT.COURSENUMBER) AS CCNUMBER, PROJECT.COURSENUMBER FROM PROJECT  GROUP BY PROJECT.COURSENUMBER ORDER BY CCNUMBER DESC");
            ps.setMaxRows(4);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                ArrayList<String> c = new ArrayList<>();
                int porcentage = (int)(result.getInt("CCNUMBER")*100/countTheses());
                c.add(result.getString("COURSENUMBER"));
                c.add(porcentage+"%");
                courses.add(c);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        return courses;
    }


    public void approve() {
        selectedUser.approveAccount();
    }

    public void deny() {

    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * @return the allProjects
     */
    public ArrayList<Project> getAllProjects() {
        approvedUsers = getApprovedUsers();
        allProjects = new ArrayList<Project>();
        int studentID;
        Project p = null;
        User u = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM project");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                p = new Project();
                u = new User();
                p.setId(result.getInt("id"));
                p.setName(result.getString("name"));
                studentID = result.getInt("student_id");
                u = findUser(studentID);
                p.setUser(u);
                p.setCourseNumber(result.getString("courseNumber"));
                p.setLiveLink(result.getString("liveLink"));
                p.setProjectAbstract(result.getString("abstract"));
                p.setScreencastLink(result.getString("screencastlink"));
                p.setSemester(result.getString("semester"));
                //p.setDateCreated(result.getString("dateCreated"));
                p.setHighlighted(result.getBoolean("highlighted"));
                allProjects.add(p);
            }

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return allProjects;
    }

    /**
     * @return the highlightedProjects
     */
    public ArrayList<Project> getHighlightedProjects() {
        return highlightedProjects;
    }

    /**
     * @return the userAmount
     */
    public int getUserAmount() {
        Connection conn = Database.connect2DB();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(id) FROM useraccount");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                userAmount = result.getInt(1);
            }

            result.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return userAmount;
    }

    public User findUser(int id) {
        for (int i = 0; i < approvedUsers.size(); i++) {
            if (approvedUsers.get(i).getId() == id) {
                return approvedUsers.get(i);
            }
        }
        return null;
    }

    public boolean toBeApproved() {

        boolean toProcess = true;

        if (getUnapprovedUsers().size() > 0) {
            toProcess = true;
        } else {
            toProcess = false;
        }

        return toProcess;

    }

}
