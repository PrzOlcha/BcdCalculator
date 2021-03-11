package controler;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet class that is used to display history of checked parameters stored in database
 * 
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 5.0
 */
@WebServlet(name = "HistoryOfParametersFromDbServlet", urlPatterns = {"/HistoryOfParametersFromDb"})
public class HistoryOfParametersFromDbServlet extends HttpServlet {

    /**
     * This method is used to create and display a table with history of checked parameters stored in a table in database
     *
     * @param request paramater used for HttpServletRequest interface
     * @param response paramater used for HttpServletResponse interface
     * @throws IOException when an error occurred during an input-output operation
     * @throws ServletException is thrown to indicate a servlet error
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html; charset=ISO-8859-2");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false); //identifier of current session
        if (session == null) {
            session = request.getSession(true);
            try {
                Class.forName(getServletContext().getInitParameter("dbDriver"));
            } catch (ClassNotFoundException cnfe) {
                out.write(cnfe.getMessage());
                return;
            }
            DatabaseConnectionHolder databaseConnectionObject = new DatabaseConnectionHolder();
            if ((databaseConnectionObject.createConnection(getServletContext().getInitParameter("dbUrl"),getServletContext().getInitParameter("dbUser"),getServletContext().getInitParameter("dbPass")))==false)
            {
                out.println("A connection to the database could not be established.</br>");
            }
            else
            {
                session.setAttribute("databaseConnectionObject", databaseConnectionObject);
            }
        }
        if ((DatabaseConnectionHolder) session.getAttribute("databaseConnectionObject") == null) {
            try {
                Class.forName(getServletContext().getInitParameter("dbDriver"));
            } catch (ClassNotFoundException cnfe) {
                out.write(cnfe.getMessage());
                return;
            }
            DatabaseConnectionHolder databaseConnectionObject = new DatabaseConnectionHolder();
            if ((databaseConnectionObject.createConnection(getServletContext().getInitParameter("dbUrl"),getServletContext().getInitParameter("dbUser"),getServletContext().getInitParameter("dbPass")))==false)
            {
                out.println("A connection to the database could not be established.</br>");
            }
            else
            {
                session.setAttribute("databaseConnectionObject", databaseConnectionObject);
            }
        }
        try
        {
            DatabaseConnectionHolder connectionObject=(DatabaseConnectionHolder)session.getAttribute("databaseConnectionObject");
            Statement statement = connectionObject.databaseConnectionInSession.createStatement();
            DatabaseMetaData dbm = connectionObject.databaseConnectionInSession.getMetaData();
            ResultSet rs = dbm.getTables(null, null, getServletContext().getInitParameter("parametersTableName").toUpperCase(), null);
            if (!rs.next())
            {
                statement.executeUpdate("CREATE TABLE "+getServletContext().getInitParameter("parametersTableName").toUpperCase()+" (parameter VARCHAR(80), properParameter VARCHAR(3))");
            }
            rs = statement.executeQuery("SELECT * FROM "+getServletContext().getInitParameter("parametersTableName").toUpperCase());
            out.write("<html><head><style>table {border-collapse: collapse;}td,th{padding:5px;}</style></head><body><table border=\"1\"><thead>"
                    + "<tr><td align=\"center\" colspan=\"2\"><b>History of checked parameters from database:</b></td></tr><tr><td>Checked paramater</td><td>Is it a valid number in BCD notation?</td></tr></thead><tbody>");
            while (rs.next()) {
                out.write("<tr><td>" + rs.getString("parameter") + "</td>");
                out.write("<td>" + rs.getString("properParameter") + "</td></tr>");
            }
            out.write("</tbody></table></body></html>");
        } catch (SQLException sqle) {
            out.println("An error occur. Table with history of checked parameters could not be displayed.</br>");
        }
        out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
    }
}
