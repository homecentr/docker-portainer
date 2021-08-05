import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.HashMap;

public class ContainerController {
    private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    private GenericContainer _container;
    private GenericContainer _dependencyContainer;
    private Network _network;

    public ContainerController() {
        _network = Network.newNetwork();
    }

    public void start(HashMap<String, String> envVars, boolean waitToStart) {
        String dockerImageTag = System.getProperty("image_tag");

        logger.info("Tested Docker image tag: {}", dockerImageTag);

        _container = new GenericContainer<>(dockerImageTag)
                .withEnv(envVars)
                .withNetwork(_network)
                .withExposedPort(9000);

        if(waitToStart) {
            _container = _container.waitingFor(Wait.forLogMessage(".*Starting Portainer.*on :9000.*", 1));
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

    protected GenericContainer getContainer() {
        return _container;
    }
}