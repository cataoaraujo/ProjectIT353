/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Project;
import java.io.IOException;
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
public class ProjectDetailsController {

    @ManagedProperty("#{param.id}")
    private int id;

    private Project project;
    
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
            }
        }
    }

    /**
     * Creates a new instance of ProjectDetailsController
     */
    public ProjectDetailsController() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    

}
