package controler;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.servlet.annotation.WebServlet;
import model.Calculator;

/**
 * Servlet class that is used to display history of calculations stored inside object of Calculator class
 * 
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 4.0
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/History"})
public class HistoryServlet extends HttpServlet {

    /**
     * This method is used to create and display a table with calculation history using a list contained in Calculator class
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
            Calculator calculator = new Calculator();
            session.setAttribute("calculator", calculator);
        }
        if ((Calculator) session.getAttribute("calculator") == null) {
            Calculator calculator = new Calculator();
            session.setAttribute("calculator", calculator);
        }
        Calculator calculatorInSession = (Calculator) session.getAttribute("calculator");
        if (calculatorInSession.resultsHistory.isEmpty()) {
            out.println("History of calculations is empty.</br>");
        } else {
            int historyRows = calculatorInSession.resultsHistory.size() / 4; //quantity of successfully performed calculations which is equal to quantity of rows that should be displayed in table
            out.write("<html><head><style>table {border-collapse: collapse;}td,th{padding:5px;}</style></head><body><table border=\"1\"><thead>"
                    + "<tr><td align=\"center\" colspan=\"4\"><b>History table:</b></td></tr><tr><td>First numbers</td><td>Second numbers</td><td>Summation results</td><td>Subtraction results</td></tr></thead><tbody>");
            for (int i = 0; i < historyRows; i++) {
                out.write("<tr><td>" + calculatorInSession.resultsHistory.get(4 * i + 0) + "</td>");
                out.write("<td>" + calculatorInSession.resultsHistory.get(4 * i + 1) + "</td>");
                out.write("<td>" + calculatorInSession.resultsHistory.get(4 * i + 2) + "</td>");
                out.write("<td>" + calculatorInSession.resultsHistory.get(4 * i + 3) + "</td></tr>");
            }
            out.write("</tbody></table></body></html>");
        }
        out.println("</br><input type=\"button\" value=\"Go back to main page\" onClick=\"location.href='/BcdCalculator';\" />");
    }
}
