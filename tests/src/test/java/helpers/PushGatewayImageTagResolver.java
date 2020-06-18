package helpers;

import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;

public class PushGatewayImageTagResolver extends EnvironmentImageTagResolver {
    public PushGatewayImageTagResolver() {
        super("homecentr/prometheus-push-gateway:local");
    }
}
