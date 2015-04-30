/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Database.Database;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author it3530203
 */
public class Submissions {

    private int id;
    private Project project;
    private InputStream document;
    StreamedContent file;
    private String type;
    private boolean approved;
    private Date dateSubmitted;
    private ArrayList<Committee> committee = new ArrayList<>();

    
    
    public StreamedContent download() {
        return file;
    }
    
    public static Submissions findByID(int id) {
        Submissions s = null;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ProjectSubmission WHERE submission_id = ? ");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                s = new Submissions();
                s.setId(id);
                s.setApproved(result.getBoolean("approved"));
                s.setType(result.getString("type"));
                getBlob(s);
                s.setProject(Project.findById(result.getInt("project_id")));
                s.setId(result.getInt("submission_id"));
                s.setCommittee(s.findCommittee());
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return s;
    }
    
    private static void getBlob(Submissions s){
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT document FROM ProjectSubmission WHERE submission_id = ? ");
            ps.setInt(1, s.id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Blob blob = result.getBlob("document");
                s.setDocument(blob.getBinaryStream());
                s.file = new DefaultStreamedContent(s.getDocument(), "image/jpg", "file.jpg");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    
    public boolean submit() {
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO ProjectSubmission VALUES (default, ?, ?, ?, 'False', ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, project.getId());
            ps.setBlob(2, document);
            ps.setString(3, type);
            ps.setString(4, new SimpleDateFormat("yyyy-MM-dd-kk.mm.ss.SSS").format(new Date()));
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
                rs.close();
                sendEmailApproval();
                return true;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    public static boolean isAdvisor(int committeeID, int submissionID) {
        boolean flag = false;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Committee, ProjectSubmission "
                    + "WHERE ProjectSubmission.submission_id = ? AND Committee.commitee_id = ? AND Committee.project_id = ProjectSubmission.project_id AND Committee.type = 'Advisor'");
            ps.setInt(1, submissionID);
            ps.setInt(2, committeeID);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                flag = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return flag;
    }

    public static boolean updateSubmissionStatus(int submissionID) {
        boolean flag = false;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE ProjectSubmission SET approved = 'True' WHERE submission_id = ?");
            ps.setInt(1, submissionID);
            if (ps.executeUpdate() == 1) {
                flag = true;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return flag;
    }

    public static boolean committeeApproval(int committeeID, int submissionID, boolean approval, String comment) {
        if (isAdvisor(committeeID, submissionID) && approval==true) {
            System.out.println(committeeID+" Is and Advisor");
            updateSubmissionStatus(submissionID);
        }
        boolean flag = false;
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Approval VALUES (?, ?, ?, ?)");
            ps.setInt(1, submissionID);
            ps.setInt(2, committeeID);
            ps.setBoolean(3, approval);
            ps.setString(4, comment);
            if (ps.executeUpdate() == 1) {
                flag = true;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return flag;
    }

    public ArrayList<Committee> findCommittee() {
        ArrayList<Committee> committees = new ArrayList<>();
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Committee WHERE project_id = ?");
            ps.setInt(1, project.getId());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Committee c = new Committee();
                c.setId(result.getInt("commitee_id"));
                c.setName(result.getString("committeename"));
                c.setEmail(result.getString("committeeemail"));
                switch (result.getString("type")) {
                    case "Chair":
                        c.setType(Committee.CommitteeType.Chair);
                        break;
                    case "Member":
                        c.setType(Committee.CommitteeType.Member);
                        break;
                    case "Advisor":
                        c.setType(Committee.CommitteeType.Advisor);
                        break;
                }
                this.findCommitteeComment(c);
                committees.add(c);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return committees;
    }
    
    private void findCommitteeComment(Committee committee){
        Connection conn = Database.connect2DB();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Approval WHERE submission_id = ? AND committee_id = ?");
            ps.setInt(1, this.id);
            ps.setInt(2, committee.getId());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                committee.setComment(result.getString("committeecomment"));
                committee.setApproved(result.getBoolean("approved"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public boolean sendEmailApproval() {
        boolean flag = false;
        ArrayList<Committee> committees = findCommittee();
        for (Committee c : committees) {
            sendEmail(this.project, this, c);
        }
        return flag;
    }

    private void sendEmail(Project project, Submissions submission, Committee c) {
        // Recipient's email ID needs to be mentioned.
        String to = c.getEmail() + "@ilstu.edu";

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
            message.setSubject("You are requested to Approve the Project: " + project.getName());

            // Send the actual HTML message, as big as you like
            String msg = "Link to the project: http://localhost:8080/ISURepository/secure/projectDetails.xhtml?id=" + project.getId() + "<br />";
            msg += "Link to approve the project: http://localhost:8080/ISURepository/Approval.xhtml?cID=" + c.getId() + "&sID=" + submission.id + " <br />";
            message.setContent(msg, "text/html");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

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

    public InputStream getDocument() {
        return document;
    }

    public void setDocument(InputStream document) {
        this.document = document;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public ArrayList<Committee> getCommittee() {
        return committee;
    }

    public void setCommittee(ArrayList<Committee> committee) {
        this.committee = committee;
    }
   
}
