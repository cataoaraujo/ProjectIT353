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
import java.text.SimpleDateFormat;
//import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
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
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Project VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, false)",Statement.RETURN_GENERATED_KEYS);
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
                }

                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
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
        return liveLink;
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
