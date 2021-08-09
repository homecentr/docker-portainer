import helpers.DockerImageTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.images.NeverPullImagePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.time.Duration;
import java.util.HashMap;

public class ContainerController {
    private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    private GenericContainerEx _container;
    private GenericContainer _dependencyContainer;
    private Network _network;

    public ContainerController() {
        _network = Network.newNetwork();
    }

    public void start(HashMap<String, String> envVars, boolean waitToStart) {
        _container = new GenericContainerEx<>(new DockerImageTagResolver())
                .withEnv(envVars)
                .withNetwork(_network)
                .withExposedPorts(9000)
                .withImagePullPolicy(new NeverPullImagePolicy());

        if(waitToStart) {
            _container = (GenericContainerEx) _container.waitingFor(Wait.forLogMessage(".*Starting Portainer.*on :9000.*", 1));
        }
        else {
            // Wait strategy must be set explicitly otherwise testcontainers wait for an unspecified port
            // and the port check requires bash to be in the container (https://github.com/testcontainers/testcontainers-java/issues/3317)
            _container.setWaitStrategy(Wait.forLogMessage(".*", 1));
        }

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    public void startDependencyContainer(String networkAlias){
        _dependencyContainer = new GenericContainer<>("alpine")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostName(networkAlias))
                .withCreateContainerCmdModifier(cmd -> cmd.withAliases(networkAlias))
                .withNetworkAliases(networkAlias)
                .withCommand("ping 127.0.0.1")
                .withNetwork(_network);

        _dependencyContainer.start();
        _dependencyContainer.followOutput(new Slf4jLogConsumer(logger));
    }

    public void stopIfRunning() {
        _container.stop();
        _container.close();

        if(_dependencyContainer != null) {
            _dependencyContainer.stop();
            _dependencyContainer.close();
        }
    }

    protected GenericContainerEx getContainer() {
        return _container;
    }
}