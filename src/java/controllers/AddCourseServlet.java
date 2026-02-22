/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;

public class AddCourseServlet extends HttpServlet {
    
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
        String instructorID = (String) session.getAttribute("userID");
        String courseID = request.getParameter("courseID");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String startTime = request.getParameter("startTime") + ":00";
        String endTime = request.getParameter("endTime") + ":00";
        
        System.out.println("SDate: " + startDate);
        System.out.println("EDate: " + endDate);
        System.out.println("STime: " + startTime);
        System.out.println("ETime: " + endTime);
        
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            System.out.println("Format STime: " + inputTimeFormat.format(inputTimeFormat.parse(startTime)));            
            System.out.println("Format ETime: " + inputTimeFormat.format(inputTimeFormat.parse(endTime)));            
        } catch (Exception ex) {
            
        }
        
        String query = "INSERT INTO Instructor_Courses (InstructorID, CourseID) VALUES (?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, instructorID);
            ps.setString(2, courseID);
            ps.executeUpdate();
            
            query = "SELECT LAST_INSERT_ID();";
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int instructorCourseID = rs.getInt(1);
            
            query = "INSERT INTO Schedule (InstructorCourseID, StartDate, EndDate, StartTime, EndTime) VALUES (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(query);
            ps.setInt(1, instructorCourseID);
            //Date format should be: YYYY-MM-DD
            ps.setString(2, inputDateFormat.format(inputDateFormat.parse(startDate)));
            ps.setString(3, inputDateFormat.format(inputDateFormat.parse(endDate)));
            //Time format should be: HH:MM:ss
            ps.setString(4, inputTimeFormat.format(inputTimeFormat.parse(startTime)));
            ps.setString(5, inputTimeFormat.format(inputTimeFormat.parse(endTime)));
            ps.executeUpdate();
            
            rs.close();
            ps.close();
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateCourseException("Duplicate Page");
        } catch (SQLException | ParseException sqle) {
            sqle.printStackTrace();
        }
        response.sendRedirect("DatabaseServlet");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
