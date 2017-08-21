package com.eyeline.mnp.web;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.nio.file.Paths;

/**
 * @author Chukanov
 */
public class RestServer {
    private static Storage storage;
    static Storage getStorage() {
        return storage;
    }

    public static void main(String[] args) throws Exception {
        Builder builder = Builder.builder();
        storage = builder.
                add(new RossvyazMasksParser(Paths.get("./config/rossvyaz/ru.backup.2017-08-09-11-45.csv"))).
                add(new CustomMasksParser(Paths.get("./config/mnos.xml"))).
                add(new ZniisMnpParser(Paths.get("./config/zniis/"))).
                idTitle(Paths.get("./config/filters/titles.xml")).
                idRegion(Paths.get("./config/filters/areas.xml")).
                build();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(8080);

        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                MnpApi.class.getCanonicalName());
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
