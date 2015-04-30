/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Rodrigo
 */
public class Database {

    public static Connection connect2DB() {
        Connection DBConn = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            DBConn = DriverManager.getConnection("jdbc:derby://gfish2.it.ilstu.edu:1527/rcataoa_ISURepository", "itkstu", "gopher");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return DBConn;
    }

}
