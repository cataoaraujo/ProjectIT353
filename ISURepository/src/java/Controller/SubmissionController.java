/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Project;
import Model.Submissions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Rodrigo
 */
@ManagedBean
@RequestScoped
public class SubmissionController {

    private int id;
    private String type;

    private Project project;
    private UploadedFile file;

    public void cleanError(ComponentSystemEvent event) {
        project = Project.findById(id);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void upload() {
        try {
            project = Project.findById(id);
            System.out.println(id);
            Submissions s = new Submissions();
            s.setProject(project);
            s.setDocument(file.getInputstream());
            s.setType(type);
            if (s.submit()) {
                System.out.println("Submitted!");
            }
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                response.sendRedirect("myProjects.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
