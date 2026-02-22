/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class PdfGenerator extends HttpServlet {

    //byte[] keyFromDD;
    //String cipherFromDD, ciphertype;
    Connection con;
    Connection con2;
    public int BUFFER_SIZE = 1024 * 1000;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            //Get key and cipher from DD
            //keyFromDD = getServletContext().getInitParameter("key").getBytes();
            //cipherFromDD = getServletContext().getInitParameter("cipher");
            //ciphertype = getServletContext().getInitParameter("ciphertype");
            // First database connection
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

// Second database connection
            Class.forName(config.getInitParameter("jdbcClassName2"));
            String username2 = config.getInitParameter("dbUserName2");
            String password2 = config.getInitParameter("dbPassword2");
            StringBuffer url2 = new StringBuffer(config.getInitParameter("jdbcDriverURL2"))
                    .append("://")
                    .append(config.getInitParameter("dbHostName2"))
                    .append(":")
                    .append(config.getInitParameter("dbPort2"))
                    .append("/")
                    .append(config.getInitParameter("databaseName2"))
                    .append("?autoReconnect=true&useSSL=false");
            con2 = DriverManager.getConnection(url2.toString(), username2, password2);
        } catch (SQLException sqle) {
            System.out.println("SQLException error occured - "
                    + sqle.getMessage());
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occured - "
                    + nfe.getMessage());
        }
    }

    Font[] fonts = {
        new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD),
        new Font(Font.FontFamily.HELVETICA, Font.DEFAULTSIZE, Font.BOLD),
        new Font(Font.FontFamily.HELVETICA, Font.DEFAULTSIZE, Font.ITALIC, BaseColor.WHITE),
        new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, BaseColor.WHITE),
        new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD, BaseColor.WHITE),
        new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.WHITE),
        new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD),
        new Font(Font.FontFamily.HELVETICA, 12),};

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("role");
        String userID = (String) session.getAttribute("userID");
        String userList = request.getParameter("userList");
        String adminReport = request.getParameter("adminReport");
        String reportType = null;
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = df.format(date);

        try {
            if (adminReport != null && adminReport.equals("true")) {
                reportType = "AdminPersonalRecord";
                AdminReport(user, userRole, fileName, userList, adminReport, userID);
            } else if (userRole.equals("Admin") && (userList != null && userList.equals("true"))) {
                reportType = "AdminListOfUser";
                AdminList(user, userRole, fileName, userList, adminReport, userID);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Code segment to open generated PDF report    
        String filePath = getServletContext().getRealPath("/") + reportType + "_" + fileName + ".pdf"; // Change this to the path of your PDF file

        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + reportType + "_" + fileName + ".pdf");
        try (ServletOutputStream sos = response.getOutputStream()) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                sos.write(buffer, 0, bytesRead);
            }
        }

        // Code segment to download generated PDF report
        /*String filePath = getServletContext().getRealPath("/") + userRole + "_" + fileName + ".pdf";
        
        File file = new File(filePath);
        OutputStream os = null;
        FileInputStream fis = null;
        
        response.setHeader("Content-Disposition", "inline;filename=" + userRole + "_" + fileName + ".pdf");
        response.setContentType("application/octet-stream");
        
        if (file.exists()) {
            os = response.getOutputStream();
            fis = new FileInputStream(file);
            byte[] bf = new byte[BUFFER_SIZE];
            int byteRead = -1;
            while ((byteRead = fis.read(bf)) != -1) {
                os.write(bf, 0, byteRead);
            }
        }
        else {
            System.out.println("File not found");
        }*/
    }

    private void AdminReport(String user, String userRole, String fileName, String userList, String adminReport, String userID) {

        try {
            String reportType = "AdminPersonalRecord";
            Document doc = new Document();
            Rectangle rect = new Rectangle(592, 400);
            doc.setPageSize(rect);
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, pdfOutputStream);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(user, userList, adminReport);
            writer.setPageEvent(event);

            doc.open();

            Paragraph adminDetails = new Paragraph();
            adminDetails.add(new Chunk("Username: ", fonts[6]));
            adminDetails.add(new Chunk(user + "\n", fonts[7]));
            adminDetails.add(new Chunk("User Role: ", fonts[6]));
            adminDetails.add(new Chunk(userRole + "\n", fonts[7]));
            adminDetails.add(new Chunk("User ID: ", fonts[6]));
            adminDetails.add(new Chunk(userID, fonts[7]));

            Paragraph head = new Paragraph("Handled Courses", fonts[0]);
            head.setAlignment(1);
            head.setSpacingAfter(5);

            PdfPTable table = new PdfPTable(5);
            table.setWidths(new float[]{0.7f, 2f, 1.2f, 1.2f, 2f});
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            //Set headers
            PdfPCell headerCell = new PdfPCell(new Phrase("IC-ID", fonts[1]));
            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setBackgroundColor(new BaseColor(25, 148, 202, 255));
            table.addCell(headerCell);

            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setPhrase(new Phrase("CourseName", fonts[1]));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("StartDate", fonts[1]));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("EndDate", fonts[1]));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Time", fonts[1]));
            table.addCell(headerCell);

            String query = "SELECT \n"
                    + "    CONCAT(Instructor_Courses.InstructorCourseID) AS \"IC-ID\", \n"
                    + "    Courses.CourseName, \n"
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

            PreparedStatement ps;
            ps = con2.prepareStatement(query);

            ps.setString(1, userID); // Set the UserID parameter for the query
            ResultSet rs = ps.executeQuery();

            int x = 1;
            while (rs.next()) {
                String icid = rs.getString("IC-ID");
                String courseName = rs.getString("CourseName");
                String startDate = rs.getString("StartDate");
                String endDate = rs.getString("EndDate");
                String time = rs.getString("Time");

                if (x % 2 == 0) {
                    headerCell.setBackgroundColor(new BaseColor(215, 215, 215));
                } else {
                    headerCell.setBackgroundColor(new BaseColor(200, 200, 200));
                }
                x++;
                headerCell.setPhrase(new Phrase(icid));
                table.addCell(headerCell);

                headerCell.setPhrase(new Phrase(courseName));
                table.addCell(headerCell);

                headerCell.setPhrase(new Phrase(startDate));
                table.addCell(headerCell);

                headerCell.setPhrase(new Phrase(endDate));
                table.addCell(headerCell);

                headerCell.setPhrase(new Phrase(time));
                table.addCell(headerCell);
            }

            Paragraph studentList = new Paragraph("Students", fonts[0]);
            studentList.setAlignment(1);
            studentList.setSpacingAfter(5);

            PdfPTable table2 = new PdfPTable(4);
            table2.setWidths(new float[]{1f, 2f, 2f, 1f});
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            //Set headers
            PdfPCell headerCell2 = new PdfPCell(new Phrase("Student ID", fonts[1]));
            headerCell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell2.setBackgroundColor(new BaseColor(25, 148, 202, 255));
            table2.addCell(headerCell2);

            headerCell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell2.setPhrase(new Phrase("Student Name", fonts[1]));
            table2.addCell(headerCell2);
            headerCell2.setPhrase(new Phrase("Course Name", fonts[1]));
            table2.addCell(headerCell2);
            headerCell2.setPhrase(new Phrase("Progress Percentage", fonts[1]));
            table2.addCell(headerCell2);

            String query2 = "SELECT "
                    + "    Enrollments.StudentID,"
                    + "    CONCAT(Users.Username) AS 'StudentName',"
                    + "    Courses.CourseName,"
                    + "    Enrollments.ProgressPercentage\n"
                    + "FROM Users\n"
                    + "JOIN Enrollments ON Users.UserID = Enrollments.StudentID\n"
                    + "JOIN Instructor_Courses ON Enrollments.InstructorCourseID = Instructor_Courses.InstructorCourseID\n"
                    + "JOIN Courses ON Instructor_Courses.CourseID = Courses.CourseID\n"
                    + "WHERE Instructor_Courses.InstructorID = ?";

            ps = con2.prepareStatement(query2);
            ps.setString(1, userID);

            ps.setString(1, userID); // Set the UserID parameter for the query
            ResultSet rs2 = ps.executeQuery();

            int y = 1;
            while (rs2.next()) {
                String studid = rs2.getString("StudentID");
                String studName = rs2.getString("StudentName");
                String courseName = rs2.getString("CourseName");
                String progress = rs2.getString("ProgressPercentage");

                if (y % 2 == 0) {
                    headerCell2.setBackgroundColor(new BaseColor(215, 215, 215));
                } else {
                    headerCell2.setBackgroundColor(new BaseColor(200, 200, 200));
                }
                y++;
                headerCell2.setPhrase(new Phrase(studid));
                table2.addCell(headerCell2);

                headerCell2.setPhrase(new Phrase(studName));
                table2.addCell(headerCell2);

                headerCell2.setPhrase(new Phrase(courseName));
                table2.addCell(headerCell2);

                headerCell2.setPhrase(new Phrase(progress));
                table2.addCell(headerCell2);
            }

            doc.add(adminDetails);
            doc.add(head);
            doc.add(table);
            doc.newPage();
            doc.add(studentList);
            doc.add(table2);
            doc.close();

            byte[] pdfAsBytes = pdfOutputStream.toByteArray();
            addFooter(pdfAsBytes, user, userRole, fileName, userList, adminReport, reportType);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    private void AdminList(String user, String userRole, String fileName, String userList, String adminReport, String userID) {
        try {
            String reportType = "AdminListOfUser";
            Document doc = new Document(PageSize.LETTER.rotate());
            doc.setMargins(36, 36, 72, 72); // left, right, top, bottom\
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, pdfOutputStream);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(user, userList, adminReport);
            writer.setPageEvent(event);

            doc.open();
            Paragraph adminDetails = new Paragraph();
            adminDetails.add(new Chunk("Accessed By: ", fonts[6]));
            adminDetails.add(new Chunk(user + "\n\n", fonts[7]));
            //Initialize table
            PdfPTable table = new PdfPTable(3);
            table.setWidths(new float[]{0.2f, 1f, 1f});
            //Set headers
            PdfPCell headerCell = new PdfPCell(new Phrase("No.", fonts[1]));
            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setBackgroundColor(new BaseColor(25, 148, 202, 255));
            table.addCell(headerCell);

            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setPhrase(new Phrase("Username", fonts[1]));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Role", fonts[1]));
            table.addCell(headerCell);

            //Get data
            String query = "SELECT userid, username, role FROM USER_INFO";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            int x = 1;
            while (rs.next()) {
                String id = rs.getString("userid");
                String username = rs.getString("username");
                String role = rs.getString("role");

                if (x % 2 == 0) {
                    headerCell.setBackgroundColor(new BaseColor(215, 215, 215));
                } else {
                    headerCell.setBackgroundColor(new BaseColor(200, 200, 200));
                }
                x++;
                headerCell.setPhrase(new Phrase(id));
                table.addCell(headerCell);
                if (username.equals(user)) {
                    headerCell.setPhrase(new Phrase(username + "*"));
                    table.addCell(headerCell);
                } else {
                    headerCell.setPhrase(new Phrase(username));
                    table.addCell(headerCell);
                }
                headerCell.setPhrase(new Phrase(role));
                table.addCell(headerCell);
            }
            doc.add(adminDetails);
            doc.add(table);
            doc.close();
            byte[] pdfAsBytes = pdfOutputStream.toByteArray();
            addFooter(pdfAsBytes, user, userRole, fileName, userList, adminReport, reportType);
        } catch (Exception e) {
        }
    }

    private void addFooter(byte[] pdfContent, String user, String role, String file, String userList, String adminReport, String reportType) {
        try {

            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss z");
            String dateGenerated = df.format(date);

            // Create PdfReader instance with the PDF content byte array
            PdfReader reader = new PdfReader(pdfContent);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(outputStream);
            Document document = new Document();
            document.open();
            PdfStamper stamper = new PdfStamper(reader, output);
            int pageCount = reader.getNumberOfPages();
            for (int i = 1; i <= pageCount; i++) {
                if (userList != null && userList.equals("true")) {
                    ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_LEFT, new Phrase(dateGenerated, fonts[2]), document.left() - 10, document.bottom() - 20, 0);
                    ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_RIGHT, new Phrase("Page " + i + " of " + pageCount, fonts[2]), document.right() + 200, document.bottom() - 20, 0);
                } else if (adminReport != null && adminReport.equals("true")) {
                    ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_LEFT, new Phrase(dateGenerated, fonts[5]), document.left() - 10, document.bottom() - 20, 0);
                    ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_RIGHT, new Phrase("Page " + i + " of " + pageCount, fonts[5]), document.right() + 20, document.bottom() - 20, 0);
                }
            }

            stamper.close();
            document.close();
            // Get the final PDF content as byte array
            byte[] finalPdfAsBytes = outputStream.toByteArray();

            FileOutputStream fos = new FileOutputStream(getServletContext().getRealPath("/") + reportType + "_" + file + ".pdf");
            fos.write(finalPdfAsBytes);
            fos.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public class HeaderFooterPageEvent extends PdfPageEventHelper {

        private String user, userList, adminReport;
        Image image = null;
        String logo = getServletContext().getRealPath("/") + "WEB-INF\\classes\\resources\\logo.png";
        String footer = getServletContext().getRealPath("/") + "WEB-INF\\classes\\resources\\footer.png";

        public HeaderFooterPageEvent(String user, String userList, String adminReport) {
            this.user = user;
            this.userList = userList;
            this.adminReport = adminReport;
        }

        public void onStartPage(PdfWriter writer, Document doc) {

            float width = doc.getPageSize().getWidth();
            float height = doc.getPageSize().getHeight();
            float centerX = width / 2;
            if (userList != null && userList.equals("true")) {
                try {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    Rectangle rect = new Rectangle(doc.left() - 50, doc.top() + 20, doc.right() + 50, height);
                    rect.setBackgroundColor(new BaseColor(37, 35, 35));
                    canvas.rectangle(rect);
                    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("List of Users", fonts[3]), centerX, doc.top() + 35, 0);

                    image = Image.getInstance(logo);
                    image.scalePercent(50f, 50f);
                    image.setAbsolutePosition(25f, 566f);
                    canvas.addImage(image);
                    image = Image.getInstance(footer);
                    image.scalePercent(25, 15);
                    image.setAbsolutePosition(0, -1);
                    canvas.addImage(image);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (adminReport != null && adminReport.equals("true")) {
                try {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    Rectangle rect = new Rectangle(doc.left() - 50, doc.top(), doc.right() + 50, height);
                    rect.setBackgroundColor(new BaseColor(37, 35, 35));
                    canvas.rectangle(rect);
                    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Personal Record", fonts[4]), centerX, doc.top() + 10, 0);

                    image = Image.getInstance(logo);
                    image.scalePercent(30f, 30f);
                    image.setAbsolutePosition(25f, 370f);
                    canvas.addImage(image);
                    image = Image.getInstance(footer);
                    image.scalePercent(20, 15);
                    image.setAbsolutePosition(0, -1);
                    canvas.addImage(image);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
