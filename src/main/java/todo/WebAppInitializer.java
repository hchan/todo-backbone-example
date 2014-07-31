package todo;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * This class replace the "old" web.xml and is automatically scanned at the application startup
 */
public class WebAppInitializer implements WebApplicationInitializer {
	public static XmlWebApplicationContext appContext;
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    	try {
			org.h2.tools.Server.createWebServer(null).start();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	appContext = new XmlWebApplicationContext();
        appContext.getEnvironment().setActiveProfiles("resthub-jpa", "resthub-web-server", "resthub-client-logging");
        String[] locations = { "classpath*:resthubContext.xml", "classpath*:applicationContext.xml" };
        appContext.setConfigLocations(locations);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");

        servletContext.addListener(new ContextLoaderListener(appContext));
    }
}
