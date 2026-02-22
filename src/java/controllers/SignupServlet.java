package controllers;

import java.io.IOException;
import java.sql.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.DatabaseHandler;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Joshua
 */
public class SignupServlet extends HttpServlet {

    Connection con;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            //Connection
            Class.forName(config.getInitParameter("jdbcClassName"));
            System.out.println("Test2");

            String username = config.getInitParameter("dbUserName");
            String password = config.getInitParameter("dbPassword");
            StringBuffer url = new StringBuffer(config.getInitParameter("jdbcDriverURL"))
                    .append("://")
                    .append(config.getInitParameter("dbHostName"))
                    .append(":")
                    .append(config.getInitParameter("dbPort"))
                    .append("/")
                    .append(config.getInitParameter("databaseName"));
            con = DriverManager.getConnection(url.toString(), username, password);
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

        String usernameInput = request.getParameter("username");
        String passwordInput = request.getParameter("password");
        String confPasswordInput = request.getParameter("confPassword");
        String roleInput = request.getParameter("selectRole");

        try {
            String checkQuery = "SELECT * FROM USER_INFO WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(checkQuery);
            ps.setString(1, usernameInput);
            ResultSet rs = ps.executeQuery();
            
            if (!confPasswordInput.equals(passwordInput)) {
                throw new PassNotMatchException("Passwords do not match.");
            }

            if (rs.next()) {
                throw new UsernameExistsException("Wrong Password");
            } else {
                String query = "INSERT INTO USER_INFO (Username, Password, Role) VALUES (?, ?, ?)";
                ps = con.prepareStatement(query);
                ps.setString(1, usernameInput);
                ps.setString(2, passwordInput);
                ps.setString(3, roleInput);
                ps.executeUpdate();
                response.sendRedirect("index.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
