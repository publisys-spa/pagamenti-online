package it.publisys.pagamentionline.servlet;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author mcolucci
 */
public class ShutdownServlet
    extends HttpServlet {

    @Override
    public void destroy() {
        super.destroy();

        String prefix = getClass().getSimpleName() + " destroy() ";
        ServletContext ctx = getServletContext();
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver _d = drivers.nextElement();
                DriverManager.deregisterDriver(_d);
            }
        } catch (SQLException e) {
            ctx.log(prefix + "Exception caught while deregistering JDBC drivers", e);
        }
        ctx.log(prefix + "complete");
    }
}
