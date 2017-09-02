package com.eyeline.mnp.web;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.nio.file.Path;
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
        int port = getPort(args);
        Path configDir = getConfigDir(args);
        System.out.println("Configuring MNP storage from config directory: "+configDir);

        Builder builder = Builder.builder();
        storage = builder.
                add(new RossvyazMasksParser(configDir.resolve("rossvyaz/Kody_DEF-9kh.csv"))).
                add(new CustomMasksParser(configDir.resolve("mnos.xml"))).
                //add(new ZniisMnpParser(configDir.resolve("zniis/"))).
                idTitle(configDir.resolve("filters/titles.xml")).
                idRegion(configDir.resolve("filters/areas.xml")).
                build();
        System.out.println("Starting server on port: "+port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(port);

        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                MnpApi.class.getCanonicalName());
        try {
            jettyServer.start();
            System.out.println("Server started. Example request: http://localhost:"+port+"/mnp?subscriber=79139367911");
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }

    private static int getPort(String[] args) {
        int port = 8080;
        if (args.length>0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e){
                System.err.println("first parameter should be integer (server port)");
            }
        }
        return port;
    }

    private static Path getConfigDir(String[] args) {
        if (args.length>1 && args[1]!=null) {
            return Paths.get(args[1]);
        }
        return Paths.get("./config");
    }
}
