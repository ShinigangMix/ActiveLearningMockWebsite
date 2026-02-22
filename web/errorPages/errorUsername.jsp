<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Page</title>
        <link rel="stylesheet" href="styles.css">
        <link rel="stylesheet" href="styles_errorPages.css">
    </head>
    <body>
        
        <header>
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
            
                
                <div class="errorMsg">
                        <img class="exclamation" src="https://cdn-icons-png.flaticon.com/128/9392/9392685.png">
                    <h1 id="errorMessage">Username not found</h1>
                    <a href="<%=request.getContextPath()%>/index.jsp"><button type="button">Try Again</button></a>
                </div>
        <footer>
            
            <label><%= getServletContext().getInitParameter("rights")%></label>
        </footer>
        
    </body>
</html>