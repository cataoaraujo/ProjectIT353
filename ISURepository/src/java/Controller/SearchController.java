package Controller;

import Model.Project;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SearchController {

    private Project projects;
    private String query;
    private ArrayList<Project> result;

    public SearchController() {
        projects = new Project();
        if (query != null && query.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = Project.findByKeyword(query);
        }
    }

    public ArrayList<Project> findProjects() {
        result = Project.findByKeyword(query);
        return result;
    }

    public ArrayList<Project> findShocase() {
        result = Project.findShocase();
        return result;
    }

    public ArrayList<Project> findNewProjects() {
        result = Project.findNewProjects();
        return result;
    }

    public Project getProjects() {
        return projects;
    }

    public void setProjects(Project projects) {
        this.projects = projects;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<Project> getResult() {
        return result;
    }

    public void setResult(ArrayList<Project> result) {
        this.result = result;
    }

}
