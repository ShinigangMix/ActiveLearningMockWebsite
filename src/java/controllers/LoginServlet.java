/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class LoginServlet extends HttpServlet {

    byte[] keyFromDD;
    String cipherFromDD, ciphertype;
    Connection con;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            //Get key and cipher from DD
            keyFromDD = getServletContext().getInitParameter("key").getBytes();
            cipherFromDD = getServletContext().getInitParameter("cipher");
            ciphertype = getServletContext().getInitParameter("ciphertype");
            
            //Connection
            Class.forName(config.getInitParameter("jdbcClassName"));
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

            //Convesrion of Newly Entered Password to Encrypted
            String queryAll = "SELECT username, password FROM USER_INFO";
            PreparedStatement ps = con.prepareStatement(queryAll);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                
                if (rs.getString("password").length() < 16) {
                    System.out.println("Password (Original):" + rs.getString("password"));
                    String encrypStr = encrypt(rs.getString("password"), cipherFromDD, keyFromDD, ciphertype);
                    System.out.println("Password (Encrypted):" + encrypStr);
                    
                    String update = "UPDATE USER_INFO SET password = ? WHERE username = ?";
                    ps = con.prepareStatement(update);
                    ps.setString(1, encrypStr);
                    ps.setString(2, rs.getString("username"));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException sqle) {
            System.out.println("SQLException error occured - "
                    + sqle.getMessage());
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occured - "
                    + nfe.getMessage());
        }
    }
     public static String encrypt(String strToEncrypt, String cipherParameter, byte[] keyParameter, String ciphertype) {
        String encryptedString = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherParameter);
            final SecretKeySpec secretKey = new SecretKeySpec(keyParameter, ciphertype);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return encryptedString;
    }
     public static String decrypt(String codeDecrypt, String cipherParameter, byte[] keyParameter, String ciphertype) {
        String decryptedString = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherParameter);
            final SecretKeySpec secretKey = new SecretKeySpec(keyParameter, ciphertype);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decryptedString = new String(cipher.doFinal(Base64.decodeBase64(codeDecrypt)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return decryptedString;
    }
    
 protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String usernameInput = request.getParameter("username");
            String passwordInput = request.getParameter("password");

            String query = "SELECT * FROM USER_INFO WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, usernameInput);
            ResultSet rs = ps.executeQuery();
            boolean resultBool = rs.next();
            
            if (isEmptyOrNull(usernameInput) && isEmptyOrNull(passwordInput)) {
                throw new NullValueException("Blank Fields");
            } else if (!resultBool && isEmptyOrNull(passwordInput)) {
                throw new WUnameBPassException("Username Not Found and Blank Password");
                
            } else if (resultBool) {
                query = "SELECT * FROM USER_INFO WHERE username = ? AND password = ?";
                ps = con.prepareStatement(query);
                ps.setString(1, usernameInput);
                
                System.out.println("Password (DB Encrypted):" + rs.getString("password"));
                String encryptPass = encrypt(passwordInput, cipherFromDD, keyFromDD, ciphertype);
                System.out.println("Password (Input Encrypted):" + encryptPass);
                
                ps.setString(2, encryptPass);
                rs = ps.executeQuery();
                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("role", rs.getString("role"));
                    session.setAttribute("username", rs.getString("username"));
                    session.setAttribute("userID", rs.getString("userid"));
                    session.setAttribute("passwordEncrypted", encryptPass);
                    session.setAttribute("password", decrypt(encryptPass, cipherFromDD, keyFromDD, ciphertype));
                    DatabaseHandler.copyTable(true);
                    response.sendRedirect("DatabaseServlet");
                } else if (!rs.next()) {
                    System.out.println("Wrong Password");
                    throw new WrongPassException("Wrong Password");
                }
            } else {
                System.out.println("Wrong Both");
                throw new AuthenticationException("Wrong Username and Pass");
            }
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
   private boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
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
