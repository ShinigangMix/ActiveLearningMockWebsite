package listeners;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DateTimeContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) 
    {
        ServletContext context = sce.getServletContext();
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        context.setAttribute("date", df.format(date));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
    }
}
