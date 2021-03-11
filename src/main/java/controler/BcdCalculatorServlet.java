package controler;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import model.Calculator;
import model.CalculatorException;
import model.ParametersManager;

/**
 * Servlet class that is used to calculate and display results of summation and subtraction of given numbers
 * 
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2(przeolc221@stundet.polsl.pl)
 * @version 5.0
 */
@WebServlet(name = "BcdCalculatorServlet", urlPatterns = {"/Calculate"})
public class BcdCalculatorServlet extends HttpServlet {

    /**
     * This method is used to check given parameters, calculate results and save them in a list containing history of successfully performed calculations using methods conatined in Calculator and ParameterManager classes
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
            Calculator calculator = new Calculator();
            session.setAttribute("calculator", calculator);
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
        if ((Calculator) session.getAttribute("calculator") == null) {
            Calculator calculator = new Calculator();
            session.setAttribute("calculator", calculator);
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
        Calculator calculatorInSession = (Calculator) session.getAttribute("calculator");
        Cookie[] cookies = request.getCookies();
        boolean cookiesFound = false; //information whether any cookie with last used number was found, true if yes, false if not
        if (cookies == null) {
            out.println("You have not performed any calculations yet.");
        } else {
            for (Cookie tmpCookie : cookies) {
                if ((tmpCookie.getName().equals("firstNumber")) || (tmpCookie.getName().equals("secondNumber"))) {
                    if (cookiesFound == false) {
                        out.println("Last time You performed calculations for the following parameters: " + tmpCookie.getValue());
                        cookiesFound = true;
                    } else {
                        out.println(" and " + tmpCookie.getValue());
                    }
                }
            }
            if (cookiesFound == false) {
                out.println("You have not performed any calculations yet.");
            }
        }
        out.println("</br><hr>");
        if ((request.getParameter("firstNumber") == null) || (request.getParameter("secondNumber") == null)) {
            out.println("Parameters are null.</br>");
            out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
        } else {
            List<String> numbersBcd = new ArrayList<>();  //list containing parameter given by user
            numbersBcd.add(request.getParameter("firstNumber"));
            numbersBcd.add(request.getParameter("secondNumber"));
            if (numbersBcd.get(0).length() == 0 || numbersBcd.get(1).length() == 0) {
                out.println("You can't start calculations, because You did not insert two parameters.</br>");
                out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
            } else {
                Cookie firstCookie = new Cookie("firstNumber", numbersBcd.get(0));
                response.addCookie(firstCookie);
                Cookie secondCookie = new Cookie("secondNumber", numbersBcd.get(1));
                response.addCookie(secondCookie);
                boolean properParameters = parametersManagerInSession.checkParameters(numbersBcd); //information whether proper parameters have been obtained, true if yes, false if not
                if (properParameters == false) {
                    out.println("Calculations could not be performed, because parameters were not valid numbers in BCD notation.</br>");
                    out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
                } else {
                    try {
                        List<String> resultsBcd = calculatorInSession.runCalculations(numbersBcd); //list containing results of summation and subtraction in BCD notation
                        out.println("Results:</br>");
                        if (resultsBcd.get(1).charAt(0) != 'n') {
                            out.println(numbersBcd.get(0) + " + " + numbersBcd.get(1) + " = " + resultsBcd.get(0) + "</br>");
                            out.println(numbersBcd.get(0) + " - " + numbersBcd.get(1) + " = " + resultsBcd.get(1) + "</br>");
                        } else {
                            out.println(numbersBcd.get(0) + " + " + numbersBcd.get(1) + " = " + resultsBcd.get(0) + "</br>");
                            out.println("The subtraction result cannot be displayed because it's negative number and such numbers cannot be shown in BCD notation.</br>");
                        }
                        this.addToDatabase(numbersBcd, resultsBcd, session, out);
                    } catch (CalculatorException ex) {
                        out.println("</br>An error occur during performing caluclations. There were more than two parameters.</br>");
                    }
                    out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
                }
            }
        }
    }
    
    /**
     * This method is used to add results of performed calculations to database. It also creates a table (in database) if it does not already exists.
     *
     * @param numbersBcd list containing numbers which was used to perform calculations
     * @param resultsBcd list containing results of summation and subtraction
     * @param session current HTTP session
     * @param out PrintWriter used in current session
     */
    private void addToDatabase(List<String> numbersBcd, List<String> resultsBcd, HttpSession session, PrintWriter out)
    {
        try {
            DatabaseConnectionHolder connectionObject=(DatabaseConnectionHolder)session.getAttribute("databaseConnectionObject");
            Statement statement = connectionObject.databaseConnectionInSession.createStatement();
            DatabaseMetaData dbm = connectionObject.databaseConnectionInSession.getMetaData();
            ResultSet rs = dbm.getTables(null, null, getServletContext().getInitParameter("resultsTableName").toUpperCase(), null);
            if (!rs.next())
            {
                statement.executeUpdate("CREATE TABLE "+getServletContext().getInitParameter("resultsTableName").toUpperCase()+" (numberOneBcd VARCHAR(80),numberTwoBcd VARCHAR(80),resultOneBcd VARCHAR(80),resultTwoBcd VARCHAR(80))");
            }
            statement.executeUpdate("INSERT INTO "+getServletContext().getInitParameter("resultsTableName").toUpperCase()+" VALUES ('"+numbersBcd.get(0)+"','"+numbersBcd.get(1)+"','"+resultsBcd.get(0)+"','"+resultsBcd.get(1)+"')");
        } catch (SQLException sqle) {
            out.println("</br>Results could not be added to the database.</br>");
        }
    }
}