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
            <form id="loginForm" action="LoginServlet" method="POST" onsubmit="validateForm(event)">
                <h1 class="text-bold login-header">Login</h1>
                <div class="input-text">
                    <input name="username" type="text" class="textfield" placeholder="Enter your username">
                    <input name="password" type="password" class="textfield" placeholder="Enter your password">
                    <div class="g-recaptcha" data-sitekey="<%= getServletContext().getInitParameter("captchaKey")%>" data-callback="onSubmit"></div>
                </div>
                <div class="lgnbtn">
                    <input type="submit" value="Login">
                </div>
                <div class="lgnbtn" id="sign">
                    <a href="<%=request.getContextPath()%>/sign_up.jsp"><input type="Button" value="Sign Up"></a>
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
