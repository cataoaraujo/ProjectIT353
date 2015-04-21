/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.User;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@ManagedBean
@SessionScoped
public class UserController {

    private User user;
    private int attempts;
    private String msg;
    private boolean logged;

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

    public String createUser() {
        if (user.add()) {
            //sendEmail();
            logged = true;
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
                msg = "Wrong UserID or Password!";
                return "error.xhtml";
            }
        }
        msg = "You are limited to a max of 3 attempts per session, try again later!";
        return "error.xhtml";
    }

    public String update() {
        if (user.update()) {
            return "profile.xhtml";
        }
        return "error.xhtml";
    }

    private void sendEmail() {
        // Recipient's email ID needs to be mentioned.
        String to = user.getEmail();

        // Sender's email ID needs to be mentioned
        String from = "rcataoa@ilstu.edu";

        // Assuming you are sending email from this host
        String host = "smtp.ilstu.edu";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
       //properties.setProperty("mail.user", "yourID"); // if needed
        //properties.setProperty("mail.password", "yourPassword"); // if needed

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Assignment 3 Email!");

            // Send the actual HTML message, as big as you like
            message.setContent(
                    "<h1>Your new account information:</h1>"
                    + "<label>First Name: " + user.getFirstName() + "</label> <br/>\n"
                    + "<label>Last Name: " + user.getLastName() + "</label><br/>\n"
                    + "<label>User ID: " + user.getUserID() + "</label><br/>\n"
                    + "<label>Password: " + user.getPassword() + "</label><br/>\n"
                    + "<label>Email: " + user.getEmail() + "</label><br/>\n"
                    + "<label>Security Question: " + user.getSecurityQuestion() + "</label><br/>\n"
                    + "<label>Answer: " + user.getSecurityAnswer() + "</label><br/>",
                    "text/html");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    public String logoff() {
        user = new User();
        logged = false;
        return "index.xhtml";
    }

    public String logoffSecure() {
        user = new User();
        logged = false;
        return "/index.xhtml";
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
