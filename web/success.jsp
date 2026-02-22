<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="models.DatabaseHandler"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Page</title>
        <link rel="stylesheet" href="styles.css">
        <link rel="stylesheet" href="styles_successPage.css">
    </head>
    <body class="light">

        <header class="flex">
            <img class="logo" src="<%= getServletContext().getInitParameter("logoURL")%>">
            <nav>
                <ul class="links">
                    <li><a href="<%= getServletContext().getInitParameter("CoursesURL")%>">Courses</a></li>
                    <li><a href="<%= getServletContext().getInitParameter("NewsURL")%>">News</a></li>
                    <li><a href="<%= getServletContext().getInitParameter("CareersURL")%>">Careers</a></li>
                    <li><a href="<%= getServletContext().getInitParameter("AboutURL")%>">About</a></li>
                    <li><a href="<%= getServletContext().getInitParameter("ContactURL")%>">Contact</a></li>
                </ul>
            </nav>
        </header>

        <div class="main-content">
            <div class="side-panel">
                <div class="flex profile-container">
                    <img src="images/profile-user.svg" class="profile-pic"/>
                    <% System.out.println("Testing Error 500 C2");%>
                    <p class="welcome-message">Welcome, <%= session.getAttribute("username")%> <br> (<%= session.getAttribute("role")%>)</p>
                        <%System.out.println("Testing Error 500 C2.5");%>

                </div>
                <div>
                    <!-- Buttons to Choose which Table to Show -->
                    <form method="post" action="DatabaseServlet" class="flex side-panel-buttons-container">
                        <%
                            String activeTable = (String) session.getAttribute("activeTable");
                            String userRole = (String) session.getAttribute("role");
                            String tab1Header = ("Admin".equals(userRole)) ? "My Courses" : "All Courses";
                            String tab2Header = ("Admin".equals(userRole)) ? "Students" : "Current Course";
                        %>
                        <button type="submit" name="table" value="table1" class="side-panel-button text-bold <%= ("table1".equals(activeTable)) ? "active" : ""%>"><p><%= tab1Header%></p></button>
                        <button type="submit" name="table" value="table2" class="side-panel-button text-bold <%= ("table2".equals(activeTable)) ? "active" : ""%>"><p><%= tab2Header%></p></button>
                    </form>
                    <!-- Logout Button -->
                    <form action="LogoutServlet" class="flex side-panel-buttons-container">
                        <button type="submit" value="Log Out" class="side-panel-button text-bold"><p>Log Out</p></button>
                    </form>
                    <!-- ------------- -->
                </div>
            </div>
            <div class="main-panel">
                <h1><%= ("table1".equals(activeTable)) ? tab1Header : tab2Header%></h1>
                <%//For Retrieving Table Data Stored in Collection
                    List<String> columnNames = (List<String>) session.getAttribute("columnNames");
                    List<List<Object>> rowData = (List<List<Object>>) session.getAttribute("rowData");
                    if (!(userRole.equals("Student") && activeTable.equals("table2"))) {
                %>
                <div class="table-panel">
                    <%if (userRole.equals("Admin")) {%>
                    <div class="flex statistics-container">
                        <div>
                            <img class="statistics-icon" src="images/course-icon.png">
                            <h3 class="text-bold">Courses Handled: <%= session.getAttribute("TotalCourses")%></h3>
                        </div>
                        <div>
                            <img class="statistics-icon" src="images/student-icon.png">
                            <h3 class="text-bold">Students Handled: <%= session.getAttribute("TotalStudents")%></h3>
                        </div>
                    </div>
                    <% }%>

                    <table id="databaseTable">
                        <thead>
                            <tr>
                                <%
                                    for (String columnName : columnNames) {
                                %>
                                <th><%= columnName%></th>
                                    <% } %>
                                    <%if ((userRole.equals("Admin")) || (userRole.equals("Student") && activeTable.equals("table1"))) {%>
                                <th></th>
                                    <% }%>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (int i = 0; i < rowData.size(); i++) {
                                    List<Object> row = rowData.get(i);
                            %>
                            <tr class="data-row">
                                <% for (Object cell : row) {%>
                                <td><%= cell%></td>
                                <% }%>
                                <%if (userRole.equals("Admin") && activeTable.equals("table1")) {%>
                                <td>
                                    <form method="post" action="DeleteRowServlet?jspFile=success.jsp">
                                        <input type="hidden" name="rowIndex" value="<%= i%>">
                                        <input class="trash-can-icon" type="image" src="images/trash-can-icon.png">
                                    </form>
                                </td>
                                <% } else if (userRole.equals("Admin") && activeTable.equals("table2")) {%>
                                <td>
                                    <progress value="<%= row.get(3)%>" max="100"></progress>
                                </td>
                                <% } else if (userRole.equals("Student") && activeTable.equals("table1")) {%>
                                <td>
                                    <form method="post" action="ChangeCourseServlet?jspFile=success.jsp">
                                        <input type="hidden" name="rowIndex" value="<%= i%>">
                                        <input type="image" src="images/switch-icon.png">
                                    </form>
                                </td>
                                <% }%>
                            </tr>
                            <% if (userRole.equals("Student") && activeTable.equals("table1")) { %>
                            <tr class="description-row" style="display: none;">
                                <%
                                    String courseid = String.valueOf(row.get(0));
                                    String filepath = getServletContext().getRealPath("/") + "texts/" + courseid + "Description.txt";
                                    java.io.BufferedReader reader = null;
                                    StringBuilder sb = new StringBuilder();
                                    try {
                                        reader = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(filepath), "UTF-8"));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line).append("\n");
                                        }
                                    } catch (java.io.IOException e) {
                                    } finally {
                                        if (reader != null) {
                                            try {
                                                reader.close();
                                            } catch (java.io.IOException e) {
                                            }
                                        }
                                    }
                                %>
                                <td colspan="<%= row.size()%>">
                                    <p class="course-description" id="courseDesc<%= i%>"><%=sb.toString()%></p>
                                </td>
                            </tr>
                            <% } %>
                            <% }%>
                        </tbody>
                        <script>
                            document.addEventListener("DOMContentLoaded", function () {
                                var dataRows = document.querySelectorAll('.data-row');

                                dataRows.forEach(function (row) {
                                    row.addEventListener('click', function () {
                                        if ("<%= userRole%>" === "Student" && "<%= activeTable%>" === "table1") {
                                            var descriptionRow = this.nextElementSibling;
                                            descriptionRow.style.display = descriptionRow.style.display === 'none' ? '' : 'none'; // Toggle display
                                        }
                                    });
                                });
                            });
                        </script>
                    </table>

                </div>
                <% } else {
                    if (session.getAttribute("isEnrolled").equals("true")) {
                %>
                <div class="course-details-container">
                    <h4><%= rowData.get(0).get(1)%></h4>
                    <div class="std-course">

                        <div class="std-course-inst">
                            <img src="images/profile-user.svg" class="profile-pic"/>
                            <h4>Instructor </h4>
                            <p><%= rowData.get(0).get(7)%><p>
                        </div>
                        <div class="std-course-sched">
                            <h4>Schedule: </h4><p><%= rowData.get(0).get(4)%> to <%= rowData.get(0).get(5)%> at <%= rowData.get(0).get(6)%><p>
                        </div>
                        <div class="progress-section">
                            <h4>Progress: </h4><progress value="<%= rowData.get(0).get(8)%>" max="100"></progress>
                            <form action="UpdateProgressServlet?jspFile=success.jsp" method="post">
                                <input type="submit" name="progressAction" value="-">
                                <input type="submit" name="progressAction" value="+">
                            </form>
                        </div>
                    </div>
                    <h4>Course Description: </h4>
                    <p class="course-description"><%= session.getAttribute("courseDescription")%></p>
                </div>
                <% } else {%>
                <p class="course-description">To enroll in a course, click the Courses tab in the side panel to choose your course.</p>
                <%     }
                    }
                    if (userRole.equals("Admin")) {
                %>
                <div class="page-buttons">

                    <button id="openFormBtn">Add Schedule</button>
                    <div id="disableOutsideForm" class="disable-outside-form">
                        <div id="popupForm" class="popup-form"> 
                            <form action="AddCourseServlet?jspFile=success.jsp" class="add-course-form">
                                <!-- Select Course Option -->
                                <div>
                                    <label for="courseID">Course Name:</label>
                                    <select id="courseID" name="courseID" required>
                                        <option value="" disabled selected>Select a Course</option>
                                        <%
                                            List<List<Object>> courseData = DatabaseHandler.getAvailableCourses();
                                            for (int i = 0; i < courseData.size(); i++) {
                                                List<Object> courseRecord = courseData.get(i);
                                        %>
                                        <option value="<%= courseRecord.get(0)%>"><%= courseRecord.get(1)%></option>
                                        <% }%>
                                    </select>
                                </div>
                                <!-- Select Date Option -->
                                <script src="preventDateAndTime.js"></script>
                                <div>
                                    <label for="startDate">Start Date:</label>
                                    <input required type="date" id="startDate" name="startDate" placeholder="Select start date" min="<%= java.time.LocalDate.now()%>"><br><br>
                                </div>
                                <div>
                                    <label for="endDate">End Date:</label>
                                    <input required type="date" id="endDate" name="endDate" placeholder="Select end date"><br><br>
                                </div>
                                <!-- Select Time Option -->
                                <div>
                                    <label for="startTime">Start Time:</label>
                                    <input required type="time" id="startTime" name="startTime">
                                </div>
                                <div>
                                    <label for="endTime">End Time:</label>
                                    <input required type="time" id="endTime" name="endTime">
                                </div>
                                <!-- Submit Button -->
                                <div>
                                    <input type="submit" value="Done">
                                    <input required type="button" value="cancel" onclick="window.location.href = 'success.jsp'">
                                </div>
                            </form>
                        </div>
                    </div>
                    <script src="form.js"></script>
                    <div class="button">
                        <a href="PdfGenerator?userList=true">Download All Record</a>
                        <br>
                    </div>
                    <div class="button">
                        <a href="PdfGenerator?adminReport=true">Download Personal Record</a>
                        <br>
                    </div>

                </div>
                <% }%>
            </div> 
        </div>
        <footer>
            <label><%= getServletContext().getInitParameter("rights")%></label>
        </footer>
    </body>
</html>
