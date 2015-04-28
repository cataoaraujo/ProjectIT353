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
import java.text.SimpleDateFormat;
//import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author it3530203
 */
public class Project {

    private int id;
    private String name;
    private User user;
    private String courseNumber;
    private String liveLink;
    private String projectAbstract;
    private String screencastLink;
    private String semester;
    private Date dateCreated;
    private boolean highlighted;

    private ArrayList<Submissions> submissions = new ArrayList<>();
    private ArrayList<Committee> committeeMembers = new ArrayList<>();

    private ArrayList<String> keywords = new ArrayList<>();

    private int views;
    private int downloads;

    public boolean add() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Project VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, false)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, user.getId());
            ps.setString(3, courseNumber);
            ps.setString(4, liveLink);
            ps.setString(5, projectAbstract);
            ps.setString(6, screencastLink);
            ps.setString(7, semester);
            //ps.setTimestamp(8, Timestamp.from(Instant.now()));
            ps.setString(8, new SimpleDateFormat("yyyy-MM-dd-kk.mm.ss.SSS").format(new Date()));
            if (ps.executeUpdate() == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                    for (Committee committee : committeeMembers) {
                        committee.setProject(this);
                        committee.add();
                    }
                    addKeyword();//Add All Keywords
                }
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    public boolean update() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Project SET name = ?, coursenumber = ?, livelink = ?, abstract = ?, screencastlink = ?, semester = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setString(2, courseNumber);
            ps.setString(3, liveLink);
            ps.setString(4, projectAbstract);
            ps.setString(5, screencastLink);
            ps.setString(6, semester);
            ps.setInt(7, id);
            if (ps.executeUpdate() == 1) {
                removeKeywords();
                addKeyword();//Add All Keywords
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    private void removeKeywords() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM ProjectKeywords WHERE project_id = ?");
            ps.setInt(1, id);
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

    private void addKeyword() {
        Connection conn = Database.connect2DB();
        try {
            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    int kID = findKeywordID(keyword);
                    if (kID == -1) {
                        kID = createKeyword(keyword);
                    }
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO ProjectKeywords VALUES(?, ?)");
                    ps.setInt(1, kID);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public static Project findById(int id) {
        Project p = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Project WHERE id = ?");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                p = new Project();
                p.setId(result.getInt("id"));
                p.setName(result.getString("name"));
                p.setUser(User.findByID(result.getInt("student_id")));
                p.setCourseNumber(result.getString("coursenumber"));
                p.setLiveLink(result.getString("livelink"));
                p.setProjectAbstract(result.getString("abstract"));
                p.setScreencastLink(result.getString("screencastlink"));
                p.setSemester(result.getString("semester"));
                p.setDateCreated(result.getDate("datecreated"));
                p.setHighlighted(result.getBoolean("highlighted"));
                p.retrieveProjectKeywords();
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return p;
    }

    public static ArrayList<Project> findByKeyword(String keyword) {
        ArrayList<Project> projects = new ArrayList<>();
        if (keyword != null) {
            keyword = keyword.toUpperCase();

            Connection conn = Database.connect2DB();
            try {
                PreparedStatement ps = conn.prepareStatement(""
                        + "(SELECT PROJECT.ID FROM PROJECT, ProjectKeywords, Keyword "
                        + "WHERE PROJECT.ID = ProjectKeywords.project_id "
                        + "AND Keyword.id = ProjectKeywords.keyword_id "
                        + "AND UPPER(Keyword.keyword) LIKE ? )"
                        + "UNION "
                        + "(SELECT PROJECT.ID "
                        + "FROM PROJECT "
                        + "WHERE UPPER(Project.name) LIKE ? "
                        + "OR UPPER(Project.abstract) LIKE ? "
                        + "OR UPPER(Project.semester) LIKE ? "
                        + "OR UPPER(Project.courseNumber) LIKE ? )");
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ps.setString(3, "%" + keyword + "%");
                ps.setString(4, "%" + keyword + "%");
                ps.setString(5, "%" + keyword + "%");
                ps.setMaxRows(20); 
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    Project p = Project.findById(result.getInt("ID"));
                    projects.add(p);
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return projects;
    }

    public static ArrayList<Project> findShocase() {
        ArrayList<Project> projects = new ArrayList<>();

        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement(""
                    + "SELECT PROJECT.ID "
                    + "FROM PROJECT "
                    + "WHERE PROJECT.HIGHLIGHTED = true");
            ps.setMaxRows(10); 
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Project p = Project.findById(result.getInt("ID"));
                projects.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        return projects;
    }
    
    public static ArrayList<Project> findNewProjects() {
        ArrayList<Project> projects = new ArrayList<>();

        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement(""
                    + "SELECT PROJECT.ID "
                    + "FROM PROJECT "
                    + "ORDER BY PROJECT.DATECREATED");
            ps.setMaxRows(5); 
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Project p = Project.findById(result.getInt("ID"));
                projects.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        return projects;
    }

    protected void retrieveProjectKeywords() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Keyword.keyword FROM ProjectKeywords, Keyword "
                    + "WHERE Keyword.id = ProjectKeywords.keyword_id "
                    + "AND ProjectKeywords.project_id = ?");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                this.keywords.add(result.getString("keyword"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void addCommittee(Committee committee) {
        committeeMembers.add(committee);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getLiveLink() {
        if (liveLink != null) {
            liveLink = liveLink.replaceAll("http://", "");
        } else {
            return "";
        }
        return "http://" + liveLink;
    }

    public void setLiveLink(String liveLink) {
        this.liveLink = liveLink;
    }

    public String getProjectAbstract() {
        return projectAbstract;
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public String getScreencastLink() {
        return screencastLink;
    }

    public void setScreencastLink(String screencastLink) {
        this.screencastLink = screencastLink;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public void updateHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Project SET highlighted = ? WHERE id = ?");
            ps.setBoolean(1, highlighted);
            ps.setInt(2, id);
            System.out.println(id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void showcase() {
        this.highlighted = true;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Project SET highlighted = ? WHERE id = ?");
            ps.setBoolean(1, true);
            ps.setInt(2, id);
            System.out.println(id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void removeShowcase() {
        this.highlighted = false;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Project SET highlighted = ? WHERE id = ?");
            ps.setBoolean(1, false);
            ps.setInt(2, id);
            System.out.println(id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public ArrayList<Submissions> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(ArrayList<Submissions> submissions) {
        this.submissions = submissions;
    }

    public ArrayList<Committee> getCommitteeMembers() {
        return committeeMembers;
    }

    public void setCommitteeMembers(ArrayList<Committee> committeeMembers) {
        this.committeeMembers = committeeMembers;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

}
