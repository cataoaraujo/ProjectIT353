/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Database.Database;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.model.DefaultStreamedContent;

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
    private ArrayList<Project> relatedProjects;

    private ArrayList<Submissions> submissions = new ArrayList<>();
    private ArrayList<Committee> committeeMembers = new ArrayList<>();

    private ArrayList<String> keywords = new ArrayList<>();

    private int views;
    private int downloads;

    public enum Situation {

        Done, Available, Unavailable, Submitted
    }

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
                p.retrieveProjectSubmissions();
                p.retrieveProjectCommittee();
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
                    + "ORDER BY PROJECT.DATECREATED DESC");
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

    public boolean findRelatedProjects(int id) {
        ArrayList<Project> projects = new ArrayList<>();
        boolean found = false;

        System.out.println("parameter " + id);

        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT project_id FROM ProjectKeywords "
                    + " WHERE project_id != ? "
                    + " AND keyword_id "
                    + " IN(SELECT keyword_id FROM ProjectKeywords WHERE project_id = ?)");
            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.setMaxRows(3);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Project p = Project.findById(result.getInt("project_id"));
                projects.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        setRelatedProjects(projects);

        if (projects.size() > 0) {
            found = true;
        }

        return found;
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

    public String findStatusPercentage() {
        Situation s1 = presentationStatus();
        if (s1.equals(Situation.Done)) {
            return "100% Completed";
        } else {
            if (s1.equals(Situation.Submitted)) {
                return "83% Completed. Waiting the Presentation be Approved";
            }
            Situation s2 = finalStatus();
            if (s2.equals(Situation.Done)) {
                return "66% Completed. Waiting the next submission";
            } else {
                if (s2.equals(Situation.Submitted)) {
                    return "50% Completed. Waiting the Final Proposal be Approved";
                }
                Situation s3 = preliminaryStatus();
                if (s3.equals(Situation.Done)) {
                    return "33% Completed, Waiting the next submission";
                } else if (s3.equals(Situation.Submitted)) {
                    return "16% Completed. Waiting the Preliminary Proposal be Approved";
                }
            }
        }
        return "0% Completed";
    }

    public boolean isPreliminaryOk() {
        return (preliminaryStatus().equals(Situation.Submitted) || preliminaryStatus().equals(Situation.Done));
    }

    public boolean isFinalOk() {
        return (finalStatus().equals(Situation.Submitted) || finalStatus().equals(Situation.Done));
    }

    public boolean isPresentationOk() {
        return (presentationStatus().equals(Situation.Submitted) || presentationStatus().equals(Situation.Done));
    }

    public Situation preliminaryStatus() {
        Situation s = Situation.Available;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ProjectSubmission WHERE project_id = ? AND type = 'Preliminary'");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                if (result.getBoolean("approved")) {
                    s = Situation.Done;
                } else {
                    s = Situation.Submitted;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return s;
    }

    public Situation finalStatus() {
        Situation s = Situation.Unavailable;
        if (preliminaryStatus().equals(Situation.Done)) {
            Connection conn = Database.connect2DB();
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM ProjectSubmission WHERE project_id = ? AND type = 'Final'");
                ps.setInt(1, id);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    if (result.getBoolean("approved")) {
                        s = Situation.Done;
                    } else {
                        s = Situation.Submitted;
                    }
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return s;
    }

    public Situation presentationStatus() {
        Situation s = Situation.Unavailable;
        if (finalStatus().equals(Situation.Done)) {
            Connection conn = Database.connect2DB();
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM ProjectSubmission WHERE project_id = ? AND type = 'Presentation'");
                ps.setInt(1, id);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    if (result.getBoolean("approved")) {
                        s = Situation.Done;
                    } else {
                        s = Situation.Submitted;
                    }
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return s;
    }

    protected void retrieveProjectSubmissions() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ProjectSubmission WHERE project_id = ? ");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Submissions s = new Submissions();
                s.setApproved(result.getBoolean("approved"));
                s.setType(result.getString("type"));
                Blob blob = result.getBlob("document");
                s.setDocument(blob.getBinaryStream());
                s.file = new DefaultStreamedContent(s.getDocument(), "image/jpg", "file.jpg");
                s.setProject(this);
                s.setId(result.getInt("submission_id"));
                s.setCommittee(s.findCommittee());
                this.submissions.add(s);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    protected void retrieveProjectCommittee() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Committee WHERE project_id = ? ORDER BY (type = 'Chair') DESC, type DESC");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Committee c = new Committee();
                c.setId(result.getInt("commitee_id"));
                c.setProject(this);
                c.setName(result.getString("committeename"));
                c.setEmail(result.getString("committeeemail"));
                switch (result.getString("type")) {
                    case "Chair":
                        c.setType(Committee.CommitteeType.Chair);
                        break;
                    case "Member":
                        c.setType(Committee.CommitteeType.Member);
                        break;
                    case "Advisor":
                        c.setType(Committee.CommitteeType.Advisor);
                        break;
                }
                this.committeeMembers.add(c);
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
        if (screencastLink != null) {
            screencastLink = screencastLink.replaceAll("http://", "");
        } else {
            return "";
        }
        return "http://" + screencastLink;
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

    public ArrayList<Submissions> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(ArrayList<Submissions> submissions) {
        this.submissions = submissions;
    }
    
    public boolean hasSubmissions(){
        if(submissions.size() > 0)
            return true;
        else
            return false;
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
    
    public ArrayList<Project> getRelatedProjects() {
        return relatedProjects;
    }

    public void setRelatedProjects(ArrayList<Project> relatedProjects) {
        this.relatedProjects = relatedProjects;
    }

}
