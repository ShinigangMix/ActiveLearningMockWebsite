package controllers;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DeleteRowServlet extends HttpServlet {

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

        int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));

        HttpSession session = request.getSession();
        List<List<Object>> rowData = (List<List<Object>>) session.getAttribute("rowData");

        String instructorCourseID = (String) rowData.get(rowIndex).get(0);

        rowData.remove(rowIndex);
        session.setAttribute("rowData", rowData);
        
        //Admin Table 1
        try {
            // First DELETE statement
            String query = "DELETE FROM Enrollments WHERE InstructorCourseID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, instructorCourseID);
            ps.executeUpdate();

            // Second DELETE statement
            String query2 = "DELETE FROM Schedule WHERE InstructorCourseID = ?";
            ps = con.prepareStatement(query2);
            ps.setString(1, instructorCourseID);
            ps.executeUpdate();

            // Third DELETE statement
            String query3 = "DELETE FROM Instructor_Courses WHERE InstructorCourseID = ?";
            ps = con.prepareStatement(query3);
            ps.setString(1, instructorCourseID);
            ps.executeUpdate();
            
            String jspFile = request.getParameter("jspFile");
            response.sendRedirect(jspFile);
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
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
