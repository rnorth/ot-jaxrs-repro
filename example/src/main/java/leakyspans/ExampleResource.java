package leakyspans;

import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class ExampleResource {

    @GET
    @Path("normal")
    public Response normal() {
        Scope scope = GlobalTracer.get().buildSpan("unclosedSpan")
                .startActive(false);

        // ...

        scope.span().finish();
        scope.close();

        return Response.ok("OK").build();
    }

    @GET
    @Path("leaky")
    public Response leaky() {
        GlobalTracer.get().buildSpan("unclosedSpan")
                .startActive(false);

        // ...

        // we somehow forgot to finish the span!

        return Response.ok("OK").build();
    }
}
