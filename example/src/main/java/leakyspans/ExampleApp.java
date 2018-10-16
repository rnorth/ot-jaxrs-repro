package leakyspans;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.opentracing.contrib.jaxrs2.server.OperationNameProvider;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;
import io.opentracing.util.GlobalTracer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class ExampleApp extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new ExampleApp().run(args);
    }

    public void run(Configuration configuration, Environment environment) {
        ServerTracingDynamicFeature serverTracingDynamicFeature =
                new ServerTracingDynamicFeature.Builder(GlobalTracer.get())
                        .withOperationNameProvider(OperationNameProvider.ClassNameOperationName.newBuilder())
                        .build();

        environment.jersey().register(serverTracingDynamicFeature);

        FilterRegistration.Dynamic filterRegistration = environment.servlets().addFilter("tracingFilter", new SpanFinishingFilter());
        filterRegistration.setAsyncSupported(true);
        filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false);

        environment.jersey().register(ExampleResource.class);
    }
}
