package controler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Servlet class that is used to store current connection with database
 * 
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 5.0
 */
public class DatabaseConnectionHolder {
    
    /**
     * Current connection with database
     */
    public Connection databaseConnectionInSession;
    
    /**
     * This method is used to create a connection with database
     *
     * @param url URL address of database
     * @param user login used to connect to database
     * @param password password used to connect to database
     * @return boolean information whether connection was successfully established
     */
    public boolean createConnection (String url,String user,String password)
    {
        try {
            this.databaseConnectionInSession = DriverManager.getConnection(url,user,password);
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }
}