package leakyspans;

import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.junit.Assert.assertEquals;

public class LeakySpanProblemTest {

    private static MockTracer mockTracer;
    private static Client client;

    @BeforeClass
    public static void setupClass() {
        mockTracer = new MockTracer();
        GlobalTracer.register(mockTracer);

        client = ClientBuilder.newClient();
    }

    @Before
    public void resetMockTracer() {
        mockTracer.reset();
    }

    @Rule
    public DropwizardAppRule<Configuration> app = new DropwizardAppRule<>(ExampleApp.class);


    @Test
    public void normalCaseTest() {
        doTest("/normal");  // passes
    }

    @Test
    public void leakyCaseTest() {
        doTest("/leaky");   // fails: only one trace is created
    }

    private void doTest(String s) {
        client.target("http://localhost:" + app.getLocalPort() + s)
                .request()
                .get(String.class);

        long tracesAfterFirstRequest = mockTracer.finishedSpans().stream()
                .map(span -> span.context().traceId())
                .distinct()
                .count();

        assertEquals(1, tracesAfterFirstRequest);

        client.target("http://localhost:" + app.getLocalPort() + s)
                .request()
                .get(String.class);

        long tracesAfterSecondRequest = mockTracer.finishedSpans().stream()
                .map(span -> span.context().traceId())
                .distinct()
                .count();

        assertEquals(2, tracesAfterSecondRequest);
    }
}
