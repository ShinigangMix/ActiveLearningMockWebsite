package models;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    //Establish Connection
    static String urlDB1 = "jdbc:derby://localhost:1527/UserDB";
    static String usernameDB1 = "app";
    static String passwordDB1 = "app";
    static String driverDB1 = "org.apache.derby.jdbc.ClientDriver";

    static String urlDB2 = "jdbc:mysql://localhost:3306/datadb?autoReconnect=true&useSSL=false";
    static String usernameDB2 = "root";
    static String passwordDB2 = "root";
    static String driverDB2 = "com.mysql.cj.jdbc.Driver";

    static Connection conDB1, conDB2;

    public static void copyTable(boolean DB1_to_DB2) {
        try {
            Class.forName(driverDB1);
            Class.forName(driverDB2);
            conDB1 = DriverManager.getConnection(urlDB1, usernameDB1, passwordDB1);
            conDB2 = DriverManager.getConnection(urlDB2, usernameDB2, passwordDB2);
            if (DB1_to_DB2) {
                String queryDB1 = "SELECT USERNAME, ROLE, USERID FROM USER_INFO";
                String queryDB2 = "INSERT INTO Users (UserID, Username, Role) VALUES (?, ?, ?)";
                PreparedStatement psDB1 = conDB1.prepareStatement(queryDB1);
                PreparedStatement psDB2 = conDB2.prepareStatement(queryDB2);
                System.out.println("Copying Table....+");

                ResultSet resultSet = psDB1.executeQuery();
                while (resultSet.next()) {
                    // Set values for parameters
                    int userID = resultSet.getInt("UserID");
                    String username = resultSet.getString("Username");
                    String role = resultSet.getString("Role");
                    System.out.println("Copying UserID:" + userID);
                    System.out.println("Copying Username:" + username);
                    System.out.println("Copying Role:" + role);

                    PreparedStatement checkDuplicate = conDB2.prepareStatement("SELECT UserID FROM Users WHERE UserID = ?");
                    checkDuplicate.setInt(1, userID);
                    ResultSet existingRecord = checkDuplicate.executeQuery();
                    if(!existingRecord.next()){
                        psDB2.setInt(1, userID);
                        psDB2.setString(2, username);
                        psDB2.setString(3, role);
                        psDB2.executeUpdate();
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException sqle) {
            sqle.printStackTrace();
        }
    }
    public static List<List<Object>> getAvailableCourses() {
        List<List<Object>> rowData = null;
        try {
            Class.forName(driverDB2);
            conDB2 = DriverManager.getConnection(urlDB2, usernameDB2, passwordDB2);
            
            String queryDB2 = "SELECT CourseID, CourseName FROM Courses;";
            PreparedStatement psDB2 = conDB2.prepareStatement(queryDB2);

            ResultSet rs = psDB2.executeQuery();

            /////////////////////FOR STORING INTO COLLECTION////////////////////
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            //2D array for storing the data in resultset table
            rowData = new ArrayList<>();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rowData.add(row);
            }
            ////////////////////////////////////////////////////////////////////
        } catch (SQLException | ClassNotFoundException sqle) {
            sqle.printStackTrace();
        }
        return rowData;
    }
    
    public static String getCurrentEnrollment(String studentID){
        String enrollmentID = null;
        try {
            Class.forName(driverDB2);
            conDB2 = DriverManager.getConnection(urlDB2, usernameDB2, passwordDB2);
            System.out.println("Inside getCurrentEnrollment");

            String queryDB2 = "SELECT EnrollmentID FROM Enrollments WHERE Enrollments.StudentID = ?;";
            PreparedStatement psDB2 = conDB2.prepareStatement(queryDB2);
            psDB2.setString(1, studentID);
            ResultSet rs = psDB2.executeQuery();
            rs.next();

            enrollmentID = rs.getString("EnrollmentID");
            System.out.println("Current Enrollment: " + enrollmentID);
        } catch (SQLException | ClassNotFoundException sqle) {
            sqle.printStackTrace();
        }
        return enrollmentID;
    }
}
