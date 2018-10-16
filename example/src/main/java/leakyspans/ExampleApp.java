package leakyspans;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.opentracing.contrib.jaxrs2.server.OperationNameProvider;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.util.GlobalTracer;

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

        environment.jersey().register(ExampleResource.class);
    }
}
