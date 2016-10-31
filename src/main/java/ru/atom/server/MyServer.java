package ru.atom.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.atom.server.auth.AuthenticationFilter;


public class MyServer {

    public static void main(String[] args) throws Exception {
        start();
    }

    public static void start() throws Exception {
        startApi();
    }
    

    private static void startApi() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        org.eclipse.jetty.server.Server jettyServer = new org.eclipse.jetty.server.Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.server"
        );

        jerseyServlet.setInitParameter(
                "com.sun.jersey.spi.container.ContainerRequestFilters",
                AuthenticationFilter.class.getCanonicalName()
        );

        jettyServer.start();
    }

    private MyServer() {
    }
}
