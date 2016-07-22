package com.pied.piper;

import com.google.inject.Stage;
import com.google.inject.persist.PersistFilter;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * Created by akshay.kesarwan on 21/05/16.
 */
public class GalleriaApplication extends Application<GalleriaConfiguration> {

    private GuiceBundle<GalleriaConfiguration> guiceBundle;

    @Override
    public void initialize(Bootstrap<GalleriaConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<GalleriaConfiguration>newBuilder()
                .addModule(new GalleriaModule())
                .addModule(new JPADatabaseModule())
                .enableAutoConfig("com.pied.piper")
                .setConfigClass(GalleriaConfiguration.class)
                .build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(GalleriaConfiguration configuration, Environment environment) throws Exception {
        environment.servlets().addFilter("persistFilter", guiceBundle.getInjector()
                .getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        FilterRegistration.Dynamic filter =environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "*");
        filter.setInitParameter("allowCredentials", "true");

//        environment.jersey().register(new LoggingFilter(
//                        Logger.getLogger(LoggingFilter.class.getName()),
//                        true)
//        );
    }

    public static void main(String args[]) throws Exception {
        new GalleriaApplication().run(args);
    }
}
