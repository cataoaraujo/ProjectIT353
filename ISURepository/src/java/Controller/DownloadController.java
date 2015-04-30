/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Project;
import Model.Submissions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rodrigo
 */
@ManagedBean
@RequestScoped
public class DownloadController {

    private Project project;

    private Submissions submission;
    
    private int projectID;
    private int submissionID;
    
    public void init(ComponentSystemEvent event) {
        if (projectID != 0) {
            project = Project.findById(projectID);
            submission = Submissions.findByID(submissionID);
            if (project == null || submission ==null) {
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                    response.sendRedirect("../index.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    /**
     * Creates a new instance of DownloadController
     */
    public DownloadController() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Submissions getSubmission() {
        return submission;
    }

    public void setSubmission(Submissions submission) {
        this.submission = submission;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(int submissionID) {
        this.submissionID = submissionID;
    }
    
    
    
    
    
    
}
