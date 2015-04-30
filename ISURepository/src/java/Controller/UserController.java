/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.User;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

@ManagedBean
@SessionScoped
public class UserController {

    private User user;
    private int attempts;
    private String msg;
    private boolean logged;
    private String answer;

    
    public UserController() {
        user = new User();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }    
    
    public boolean haveMsg(){
        if(!msg.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    public void cleanError(ComponentSystemEvent event) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            msg = "";
            answer = "";
        }
    }

    public String createUser() {
        if (user.add()) {
            //sendEmail();
            //logged = true;
            user = new User();
            return "index.xhtml";
        }
        return "signup.xhtml";
    }

    public String login() {
        if (attempts < 3) {
            User u = User.login(user.getUserID(), user.getPassword());
            if (u != null) {
                user = u;
                logged = true;
                attempts = 0;
                return "index.xhtml";
            } else {
                attempts++;
                msg = "Wrong UserID/Password! Or account is not approved!";
                return "login.xhtml";
            }
        }
        msg = "You are limited to a max of 3 attempts per session, try again later!";
        return "login.xhtml";
    }
    
    public String retrieveInformation(){
        User u = User.findByUserID(user.getUserID());
        if(u!=null){
            user = u;
        }else{
            msg = "There is no UserID '"+user.getUserID()+"'";
        }
        return "lostpassword.xhmtl";
    }
    
    public String changePassword(){
        User u = User.findByUserID(user.getUserID());
        if(u!=null){
            if(user.getSecurityAnswer().equals(answer)){
                user.update();
                msg = "Update Successfull!";
                return "login.xhmtl";
            }else{
                msg = "Security Answer is Wrong!";
                return "lostpassword.xhmtl";
            }
        }else{
            msg = "There is no UserID '"+user.getUserID()+"'";
            return "lostpassword.xhmtl";
        }
    }

    public String update() {
        if (user.update()) {
            return "profile.xhtml";
        }
        return "error.xhtml";
    }

    public String logoff() {
        user = new User();
        logged = false;
        return "index.xhtml";
    }

    public String logoffSecure() {
        user = new User();
        logged = false;
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isAdmin() {
        if (!logged) {
            return false;
        } else if (!user.getType().equalsIgnoreCase("admin")) {
            return false;
        }
        return true;
    }

}
