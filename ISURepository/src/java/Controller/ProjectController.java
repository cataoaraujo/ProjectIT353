/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rodrigo
 */
@ManagedBean
@RequestScoped
public class ProjectController {

    private Project project;

    private String committeeChair, committeeChairEmail;
    private String committeeMember1, committeeMember1Email;
    private String committeeMember2, committeeMember2Email;
    private String committeeAdvisor, committeeAdvisorEmail;
    private String keywords;

    @ManagedProperty(value = "#{userController}")
    private UserController userController;

    @ManagedProperty("#{param.id}")
    private int id;

    public ProjectController() {
        project = new Project();
    }

    @PostConstruct
    public void init() {
        if (id != 0) {
            project = Project.findById(id);
            if (project == null) {
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                    response.sendRedirect("../index.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                ArrayList<String> ks = project.getKeywords();
                keywords = "";
                for (String k : ks) {
                    keywords += k + " ";
                }
            }
        }
    }

    public String create() {
        Committee chair = new Committee(committeeChair, committeeChairEmail, Committee.CommitteeType.Chair);

        Committee member1 = new Committee(committeeMember1, committeeMember1Email, Committee.CommitteeType.Member);
        Committee member2 = new Committee(committeeMember2, committeeMember2Email, Committee.CommitteeType.Member);

        Committee advisor = new Committee(committeeAdvisor, committeeAdvisorEmail, Committee.CommitteeType.Advisor);
        project.addCommittee(chair);
        project.addCommittee(member1);
        project.addCommittee(member2);
        project.addCommittee(advisor);
        project.setUser(userController.getUser());
        String[] ks = keywords.split(" ");
        project.setKeywords(new ArrayList<>(Arrays.asList(ks)));
        System.out.println(userController.getUser().getFirstName() + "*******************");
        if (project.add()) {
            userController.getUser().findUserProjects();
            return "myProjects.xhtml";
        }
        return "error.xhtml";
    }

    public String update() {
        project.setId(id);
        System.out.println("ID: "+id+" Project:"+project.getId());
        String[] ks = keywords.split(" ");
        project.setKeywords(new ArrayList<>(Arrays.asList(ks)));
        if (project.update()) {
            userController.getUser().findUserProjects();
            return "projectDetails.xhtml?id=" + project.getId();
        }
        return "error.xhtml";
    }

    public String getCommitteeChair() {
        return committeeChair;
    }

    public void setCommitteeChair(String committeeChair) {
        this.committeeChair = committeeChair;
    }

    public String getCommitteeChairEmail() {
        return committeeChairEmail;
    }

    public void setCommitteeChairEmail(String committeeChairEmail) {
        this.committeeChairEmail = committeeChairEmail;
    }

    public String getCommitteeMember1() {
        return committeeMember1;
    }

    public void setCommitteeMember1(String committeeMember1) {
        this.committeeMember1 = committeeMember1;
    }

    public String getCommitteeMember1Email() {
        return committeeMember1Email;
    }

    public void setCommitteeMember1Email(String committeeMember1Email) {
        this.committeeMember1Email = committeeMember1Email;
    }

    public String getCommitteeMember2() {
        return committeeMember2;
    }

    public void setCommitteeMember2(String committeeMember2) {
        this.committeeMember2 = committeeMember2;
    }

    public String getCommitteeMember2Email() {
        return committeeMember2Email;
    }

    public void setCommitteeMember2Email(String committeeMember2Email) {
        this.committeeMember2Email = committeeMember2Email;
    }

    public String getCommitteeAdvisor() {
        return committeeAdvisor;
    }

    public void setCommitteeAdvisor(String committeeAdvisor) {
        this.committeeAdvisor = committeeAdvisor;
    }

    public String getCommitteeAdvisorEmail() {
        return committeeAdvisorEmail;
    }

    public void setCommitteeAdvisorEmail(String committeeAdvisorEmail) {
        this.committeeAdvisorEmail = committeeAdvisorEmail;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserController getUserController() {
        return userController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void showcase()
    {
        this.project.updateHighlighted(true);
    }
    
    public void removeShowcase()
    {
        this.project.updateHighlighted(false);
    }

}
