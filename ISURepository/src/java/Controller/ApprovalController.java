/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Submissions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rodrigo
 */
@ManagedBean
@RequestScoped
public class ApprovalController {

    private int committeeID;
    private int submissionID;
    private String comments;

    /**
     * Creates a new instance of ApprovalController
     */
    public ApprovalController() {
    }

    public int getCommitteeID() {
        return committeeID;
    }

    public void setCommitteeID(int committeeID) {
        this.committeeID = committeeID;
    }

    public int getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(int submissionID) {
        this.submissionID = submissionID;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void approve() {
        if (Submissions.committeeApproval(committeeID, submissionID, true, comments)) {
            //Do Something that I don't know yet
        }
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.sendRedirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disapprove() {
        if (Submissions.committeeApproval(committeeID, submissionID, false, comments)) {
            //Do Something that I don't know yet
        }
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.sendRedirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
