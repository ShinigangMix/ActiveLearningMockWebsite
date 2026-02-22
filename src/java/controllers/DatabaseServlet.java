/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DatabaseServlet extends HttpServlet {

    Connection conDB1; //Still thinking how to implement --See project checklist
    Connection conDB2;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Class.forName(config.getInitParameter("jdbcClassName"));
            String usernameDB2 = config.getInitParameter("dbUserName");
            String passwordDB2 = config.getInitParameter("dbPassword");
            StringBuffer urlDB2 = new StringBuffer(config.getInitParameter("jdbcDriverURL"))
                    .append("://")
                    .append(config.getInitParameter("dbHostName"))
                    .append(":")
                    .append(config.getInitParameter("dbPort"))
                    .append("/")
                    .append(config.getInitParameter("databaseName"))
                    .append("?autoReconnect=true&useSSL=false");
            conDB2 = DriverManager.getConnection(urlDB2.toString(), usernameDB2, passwordDB2);
        } catch (SQLException sqle) {
            System.out.println("SQLException error occured - "
                    + sqle.getMessage());
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occured - "
                    + nfe.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {

            //Get Session Attributes:
            HttpSession session = request.getSession();
            String userID = (String) session.getAttribute("userID");
            String userRole = (String) session.getAttribute("role");
            String table = (String) request.getParameter("table");

            String query;
            PreparedStatement ps;
            
            query = "SELECT \n"
                    + "COUNT(DISTINCT Instructor_Courses.CourseID) AS TotalCourses,\n"
                    + "COUNT(DISTINCT Enrollments.StudentID) AS TotalStudents\n"
                    + "FROM \n"
                    + "Instructor_Courses\n"
                    + "LEFT JOIN \n"
                    + "Enrollments ON Instructor_Courses.InstructorCourseID = Enrollments.InstructorCourseID\n"
                    + "WHERE \n"
                    + "Instructor_Courses.InstructorID = ?;";

            ps = conDB2.prepareStatement(query);
            ps.setString(1, userID);
            ResultSet totals = ps.executeQuery();
            int totalCourses;
            int totalStudents;
            if (totals.next()) {
                totalCourses = totals.getInt("TotalCourses");
                totalStudents = totals.getInt("TotalStudents");

                session.setAttribute("TotalCourses", totalCourses);
                session.setAttribute("TotalStudents", totalStudents);
            }

            //Set initial value to table1 if no value set yet (null)
            if (table == null) {
                table = "table1";
            }
            //Admin Table1 - Currently Handled Courses
            if (userRole.equals("Admin") && (table.equals("table1"))) {
                query = "SELECT \n"
                        + "    CONCAT(Instructor_Courses.InstructorCourseID) AS \"IC-ID\",\n"
                        + "    CONCAT(Courses.CourseName) AS \"C-Name\",\n"
                        + "    Schedule.StartDate,\n"
                        + "    Schedule.EndDate,\n"
                        + "    CONCAT(\n"
                        + "		DATE_FORMAT(Schedule.StartTime, '%l:%i %p'), \n"
                        + "		\" to \",\n"
                        + "		DATE_FORMAT(Schedule.EndTime, '%l:%i %p')) AS \"Time\"\n"
                        + "FROM Users\n"
                        + "INNER JOIN Instructor_Courses ON Instructor_Courses.InstructorID=Users.UserID\n"
                        + "INNER JOIN Courses ON Courses.CourseID=Instructor_Courses.CourseID\n"
                        + "INNER JOIN Schedule ON Schedule.InstructorCourseID=Instructor_Courses.InstructorCourseID\n"
                        + "WHERE UserID=?;";
                ps = conDB2.prepareStatement(query);
                ps.setString(1, userID);
            } 
            //Admin Table2 - Currently Handled Students
            else if (userRole.equals("Admin") && table.equals("table2")) {
                query = "SELECT "
                        + "    Concat(Enrollments.StudentID) AS \"S-ID\",\n"
                        + "    CONCAT(Users.Username) AS \"S-Name\",\n"
                        + "    CONCAT(Courses.CourseName) AS \"C-Name\",\n"
                        + "    CONCAT(Enrollments.ProgressPercentage) AS \"Progress\"\n"
                        + "FROM Users\n"
                        + "JOIN Enrollments ON Users.UserID = Enrollments.StudentID\n"
                        + "JOIN Instructor_Courses ON Enrollments.InstructorCourseID = Instructor_Courses.InstructorCourseID\n"
                        + "JOIN Courses ON Instructor_Courses.CourseID = Courses.CourseID\n"
                        + "WHERE Instructor_Courses.InstructorID = ?";
                ps = conDB2.prepareStatement(query);
                ps.setString(1, userID);
            } 
            //Student Table1 - Available Courses
            else if (userRole.equals("Student") && table.equals("table1")) {
                query = "SELECT \n"
                        + "    CONCAT(Courses.CourseID) AS \"C-ID\", \n"
                        + "    CONCAT(Instructor_Courses.InstructorCourseID) AS \"IC-ID\", \n"
                        + "    CONCAT(Courses.CourseName) AS \"C-Name\", \n"
                        + "    Username AS \"Instructor\",\n"
                        + "    Schedule.StartDate,\n"
                        + "    Schedule.EndDate,\n"
                        + "    CONCAT(\n"
                        + "		DATE_FORMAT(Schedule.StartTime, '%l:%i %p'), \n"
                        + "		\" to \",\n"
                        + "		DATE_FORMAT(Schedule.EndTime, '%l:%i %p')) AS \"Time\"\n"
                        + "FROM Users\n"
                        + "INNER JOIN Instructor_Courses ON Instructor_Courses.InstructorID=Users.UserID\n"
                        + "INNER JOIN Courses ON Courses.CourseID=Instructor_Courses.CourseID\n"
                        + "INNER JOIN Schedule ON Schedule.InstructorCourseID=Instructor_Courses.InstructorCourseID\n"
                        + "ORDER BY Courses.CourseName;";
                ps = conDB2.prepareStatement(query);
            } 
            //Student Table2 - Current Course
            else {
                query = "SELECT \n"
                        + "    CONCAT(Courses.CourseID) AS \"C-ID\",\n"
                        + "    CONCAT(Courses.CourseName) AS \"C-Name\",\n"
                        + "    CONCAT(Courses.CourseType) AS \"Type\",\n"
                        + "    CONCAT(Courses.CourseModality) AS \"Modality\",\n"
                        + "    Schedule.StartDate,\n"
                        + "    Schedule.EndDate,\n"
                        + "    CONCAT(\n"
                        + "		DATE_FORMAT(Schedule.StartTime, '%l:%i %p'), \n"
                        + "		\" to \",\n"
                        + "		DATE_FORMAT(Schedule.EndTime, '%l:%i %p')) AS \"Time\",\n"
                        + "    CONCAT(Users.Username) AS \"Instructor\",\n"
                        + "    CONCAT(Enrollments.ProgressPercentage) AS \"Progress\"\n"
                        + "FROM Enrollments\n"
                        + "INNER JOIN Instructor_Courses ON Enrollments.InstructorCourseID = Instructor_Courses.InstructorCourseID\n"
                        + "INNER JOIN Courses ON Instructor_Courses.CourseID = Courses.CourseID\n"
                        + "INNER JOIN Users ON Instructor_Courses.InstructorID = Users.UserID\n"
                        + "INNER JOIN Schedule ON Instructor_Courses.InstructorCourseID = Schedule.InstructorCourseID\n"
                        + "WHERE Enrollments.StudentID = ?;";
                ps = conDB2.prepareStatement(query);
                ps.setString(1, userID);
                
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                {
                    String filePath = getServletContext().getRealPath("/") + "texts/" + rs.getString("C-ID") + "Description.txt"; // Path to your text file
                    ///////////////Reading Txt File///////////////
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //////////////////////////////////////////////
                    session.setAttribute("courseDescription", content.toString());
                    session.setAttribute("isEnrolled", "true");
                }
                else {
                    session.setAttribute("isEnrolled", "false");
                }
                /*
                List<List<Object>> rowData = new ArrayList<>();
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= 9; i++) {
                    row.add(rs.getObject(i));
                }
                rowData.add(row);
                  */
                response.sendRedirect("success.jsp");
            }
            ResultSet rs = ps.executeQuery();

            /////////////////////FOR DYNAMIC TABLE/////////////////////
            //Get Table Metadata (Column Numbers, Column Names) for later naming of header columns in table
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            session.setAttribute("columnNames", columnNames);
            
            //2D array for storing the data in resultset table
            List<List<Object>> rowData = new ArrayList<>();
            while (rs.next()) {

                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rowData.add(row);
            }
            session.setAttribute("rowData", rowData);
            ///////////////////////////////////////////////////////////
            session.setAttribute("activeTable", table);

            response.sendRedirect("success.jsp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
