package com.mjc.school;

import com.mjc.school.service.service.NewsServiceFactory;
import com.mjc.school.web.servlet.NewsItemServlet;
import com.mjc.school.web.servlet.NewsServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.CrossOriginHandler;

import java.util.Collections;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8083);
        server.addConnector(connector);

        CrossOriginHandler crossOriginHandler = new CrossOriginHandler();
        crossOriginHandler.setAllowedOriginPatterns(Collections.singleton("*"));
        server.setHandler(crossOriginHandler);

        ServletContextHandler context = new ServletContextHandler("/");
        crossOriginHandler.setHandler(context);

        context.addServlet(new NewsServlet(NewsServiceFactory.newsService()), "/news");
        context.addServlet(new NewsItemServlet(NewsServiceFactory.newsService()), "/news/*");

        server.start();
    }
}
