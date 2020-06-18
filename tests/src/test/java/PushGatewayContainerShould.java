import helpers.PushGatewayImageTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.HttpResponse;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class PushGatewayContainerShould {
    private static GenericContainerEx _pushGatewayContainer;
    private static final Logger logger = LoggerFactory.getLogger(PushGatewayContainerShould.class);

    private static final String JobName = "some-job";
    private static final String MetricName = "some_metrics";
    private static final String MetricValue = "3.14";

    @BeforeClass
    public static void before() {
        _pushGatewayContainer = new GenericContainerEx<>(new PushGatewayImageTagResolver())
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _pushGatewayContainer.start();
        _pushGatewayContainer.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void after() {
        _pushGatewayContainer.close();
    }

    @Test
    public void exposeMetrics() throws IOException {
        postMetrics();

        HttpResponse response = _pushGatewayContainer.makeHttpRequest(9091, "/metrics");
        String responseContent = response.getResponseContent();

        assertTrue(responseContent.contains(MetricName));
        assertTrue(responseContent.contains(MetricValue));
    }

    private void postMetrics() throws IOException {

        int mappedPort = _pushGatewayContainer.getMappedPort(9091);
        URL baseUrl = new URL(String.format("http://%s:%d", _pushGatewayContainer.getContainerIpAddress(), mappedPort));

        CollectorRegistry registry = new CollectorRegistry();
        Gauge metric = Gauge.build()
                .name(MetricName)
                .help("Some dummy metric")
                .register(registry);

        metric.set(Double.parseDouble(MetricValue));

        PushGateway gateway = new PushGateway(baseUrl);
        gateway.pushAdd(registry, JobName);
    }
}