<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Page</title>
        <link rel="stylesheet" href="styles.css">   
        <script src="https://www.google.com/recaptcha/api.js" async defer></script> 
        <script>
            function onSubmit(token) {
                document.getElementById("loginForm").submit();
            }

            function validateForm(event) {
                var recaptchaResponse = grecaptcha.getResponse();
                if (!recaptchaResponse) {
                    event.preventDefault();
                    alert("Please complete the reCAPTCHA.");
                }
            }
        </script>
    </head>
    <body>

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

        <div class="container"></div>

        <!--Login Form-->
        <div class="login">
            <form id="loginForm" action="SignupServlet" method="POST" onsubmit="validateForm(event)">
                <h1 class="text-bold login-header">Sign Up</h1>
                <div class="input-text">
                    <input name="username" type="text" class="textfield" placeholder="Enter your username">
                    <input name="password" type="password" class="textfield" placeholder="Enter your password">
                    <input name="confPassword" type="password" class="textfield" placeholder="Confirm your password">
                    <label>Select Role:<select id="selectRole" name="selectRole" required>
                        <option value="" disabled selected>Select a Role</option>
                        <option value="Admin">Admin/Instructor</option>
                        <option value="Student">Student</option>
                        </select></label>
                    <div class="g-recaptcha" data-sitekey="<%= getServletContext().getInitParameter("captchaKey")%>" <!--data-callback="onSubmit" -->></div>
                </div>

                <div class="lgnbtn">
                    <input type="submit" value="Sign Up">
                </div>
                <div class="lgnbtn">
                    <a href="<%=request.getContextPath()%>/index.jsp"><input type="Button" value="Back"></a>
                </div>
            </form>

        </div>

        <footer>
            <label><%= getServletContext().getInitParameter("rights")%></label>
        </footer>
        <%
            session.invalidate();
        %>
    </body>
</html>
