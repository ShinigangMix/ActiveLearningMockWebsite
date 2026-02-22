/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.io.PrintWriter;
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
public class UpdateProgressServlet extends HttpServlet {

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
        String enrollmentID = DatabaseHandler.getCurrentEnrollment(studentID);
        String progressAction = request.getParameter("progressAction");
        
        double minValue = 0.0;
        double maxValue = 100.0;
        
        double rateOfChange = 5.0;
        
        List<List<Object>> rowData = (List<List<Object>>) session.getAttribute("rowData");
        System.out.println("Class Type: " + rowData.get(0).get(8).getClass());
        String currentProgress = String.valueOf(rowData.get(0).get(8));
        double newProgress = Double.parseDouble(currentProgress);
        if (progressAction.equals("+")) {
            newProgress += rateOfChange;
        } else if (progressAction.equals("-")) {
            newProgress -= rateOfChange;
        }
        System.out.println("What's the problem?");
        newProgress = Math.max(minValue, Math.min(newProgress, maxValue));
        rowData.get(0).set(8, newProgress);
         try {
            PreparedStatement ps;

            String updateQuery = "UPDATE Enrollments\n"
                     + "SET ProgressPercentage = ?\n"
                     + "WHERE EnrollmentID = ?;";
            ps = con.prepareStatement(updateQuery);
            ps.setDouble(1, newProgress);
            ps.setString(2, enrollmentID);
            ps.executeUpdate();
            
            String jspFile = request.getParameter("jspFile");
            response.sendRedirect(jspFile);
        } catch (Exception sqle) {
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
