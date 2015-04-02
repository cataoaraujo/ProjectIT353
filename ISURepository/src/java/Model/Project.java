/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

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
    
    private ArrayList<Submissions> submissions;
    private ArrayList<Committee> committeeMembers;
    
    private ArrayList<String> keywords;
    
    private int views;
    private int downloads;

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
