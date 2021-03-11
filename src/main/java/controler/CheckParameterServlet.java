package controler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ParametersManager;

/**
 * Servlet class that is used to check parameter
 * 
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 5.0
 */
@WebServlet(name = "CheckParameterServlet", urlPatterns = {"/Check"})
public class CheckParameterServlet extends HttpServlet {
    
    /**
     * This method is used to check if parameter given by user is representing a number in BCD notation using method contained in ParameterManager class
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
            ParametersManager parametersManager = new ParametersManager();
            session.setAttribute("parametersManager", parametersManager);
            try {
                Class.forName(getServletContext().getInitParameter("dbDriver"));
            } catch (ClassNotFoundException cnfe) {
                out.write(cnfe.getMessage());
                return;
            }
            DatabaseConnectionHolder databaseConnectionObject = new DatabaseConnectionHolder();
            if ((databaseConnectionObject.createConnection(getServletContext().getInitParameter("dbUrl"),getServletContext().getInitParameter("dbUser"),getServletContext().getInitParameter("dbPass")))==false)
            {
                out.println("</br>A connection to the database could not be established.</br>");
            }
            else
            {
                session.setAttribute("databaseConnectionObject", databaseConnectionObject);
            } 
        }
        if ((ParametersManager) session.getAttribute("parametersManager") == null) {
            ParametersManager parametersManager = new ParametersManager();
            session.setAttribute("parametersManager", parametersManager);
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
                out.println("</br>A connection to the database could not be established.</br>");
            }
            else
            {
                session.setAttribute("databaseConnectionObject", databaseConnectionObject);
            }  
        }
        ParametersManager parametersManagerInSession = (ParametersManager) session.getAttribute("parametersManager");
        Cookie[] cookies = request.getCookies();
        boolean cookiesFound = false; //information whether a cookie with last used paramater was found, true if yes, false if not
        if (cookies == null) {
            out.println("You have not checked any parameters yet.");
        } else {
            for (Cookie tmpCookie : cookies) {    
                if (tmpCookie.getName().equals("checkedParameter")) {
                    out.println("Last time You checked the following parameter: " + tmpCookie.getValue());
                    cookiesFound = true;
                }
            }
            if (cookiesFound == false) {
                out.println("You have not checked any parameters yet.");
            }
        }
        out.println("</br><hr>");
        if (request.getParameter("number") == null) {
            out.println("Parameter is null.</br>");
            out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
        } else {
            List<String> numberBcd = new ArrayList<>(); //list containing parameter given by user
            numberBcd.add(request.getParameter("number"));
            if (numberBcd.get(0).length() == 0) {
                out.println("To check parameter You must insert it first.</br>");
                out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
            } else {
                boolean properParameter = parametersManagerInSession.checkParameters(numberBcd); //information whether proper parameters have been obtained, true if yes, false if not
                if (properParameter == false) {
                    out.println("Parameter '"+numberBcd.get(0)+"' is not a valid number in BCD notation.</br>");
                    out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
                } else {
                    out.println("Parameter '"+numberBcd.get(0)+"' is a valid number in BCD notation.</br>");
                    out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
                }
                Cookie checkCookie = new Cookie("checkedParameter", numberBcd.get(0));
                response.addCookie(checkCookie);
                this.addToDatabase(numberBcd.get(0), properParameter, session, out);
            }
        }
    }
    
    /**
     * This method is used to add results of performed calculations to database. It also creates a table (in database) if it does not already exists.
     *
     * @param checkedParamater String which stores parameter that was checked
     * @param properParameter information if given parameter is a valid numbers in BCD notation, true if number is valid, false if number not valid valid
     * @param session current HTTP session
     * @param out PrintWriter used in current session
     */
    private void addToDatabase(String checkedParamater, boolean properParameter, HttpSession session, PrintWriter out)
    {
        try {
            DatabaseConnectionHolder connectionObject=(DatabaseConnectionHolder)session.getAttribute("databaseConnectionObject");
            Statement statement = connectionObject.databaseConnectionInSession.createStatement();
            DatabaseMetaData dbm = connectionObject.databaseConnectionInSession.getMetaData();
            ResultSet rs = dbm.getTables(null, null, getServletContext().getInitParameter("parametersTableName").toUpperCase(), null);
            if (!rs.next())
            {
                statement.executeUpdate("CREATE TABLE "+getServletContext().getInitParameter("parametersTableName").toUpperCase()+" (parameter VARCHAR(80), properParameter VARCHAR(3))");
            }
            if (properParameter==true)
            {
                statement.executeUpdate("INSERT INTO "+getServletContext().getInitParameter("parametersTableName").toUpperCase()+" VALUES ('"+checkedParamater+"','Yes')");
            }
            else
            {
                statement.executeUpdate("INSERT INTO "+getServletContext().getInitParameter("parametersTableName").toUpperCase()+" VALUES ('"+checkedParamater+"','No')");
            }    
        } catch (SQLException sqle) {
            out.println("</br>Checked paramater could not be added to the database.</br>");
        }
    }
}