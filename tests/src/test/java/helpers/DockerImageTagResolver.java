package helpers;

import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;
import io.homecentr.testcontainers.images.SystemWrapperImpl;

public class DockerImageTagResolver extends EnvironmentImageTagResolver {
    public DockerImageTagResolver() {
        super(new SystemWrapperImpl(), "homecentr/portainer:local");
    }
}
