
package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.DatabaseHandler;

/**
 *
 * @author jazmi
 */
public class ChangeCourseServlet extends HttpServlet {
    
    Connection con;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            //Connection to Database 2 (MySQL)
            Class.forName(config.getInitParameter("jdbcClassName"));
            String username = config.getInitParameter("dbUserName");
            String password = config.getInitParameter("dbPassword");
            StringBuffer url = new StringBuffer(config.getInitParameter("jdbcDriverURL"))
                    .append("://")
                    .append(config.getInitParameter("dbHostName"))
                    .append(":")
                    .append(config.getInitParameter("dbPort"))
                    .append("/")
                    .append(config.getInitParameter("databaseName"))
                    .append("?autoReconnect=true&useSSL=false");
            con = DriverManager.getConnection(url.toString(), username, password);
        } catch (SQLException sqle) {
            System.out.println("SQLException error occured - " + sqle.getMessage());
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occured - " + nfe.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        String studentID = (String) session.getAttribute("userID");
        int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
        
        List<List<Object>> rowData = (List<List<Object>>) session.getAttribute("rowData");
        String instructorCourseID = String.valueOf(rowData.get(rowIndex).get(1));
        
        try {
            PreparedStatement ps;
            if(session.getAttribute("isEnrolled").equals("true")){
                String enrollmentID = DatabaseHandler.getCurrentEnrollment(studentID);
                String deleteQuery = "DELETE FROM Enrollments WHERE EnrollmentID = ?";
                ps = con.prepareStatement(deleteQuery);
                ps.setString(1, enrollmentID);
                ps.executeUpdate();
            }
            String insertQuery = "INSERT INTO Enrollments (StudentID, InstructorCourseID, ProgressPercentage) VALUES (?, ?, 0.00);";
            ps = con.prepareStatement(insertQuery);
            ps.setString(1, studentID);
            ps.setString(2, instructorCourseID);
            ps.executeUpdate();
            
            String jspFile = request.getParameter("jspFile");
            response.sendRedirect(jspFile);
        } catch (Exception sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
