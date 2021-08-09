import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.ContainerLaunchException;

import java.time.Duration;
import java.util.HashMap;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class PortainerContainerWithWaitForNsShould {
    private ContainerController _controller;

    @Before
    public void before() {
        _controller = new ContainerController();
    }

    @After
    public void after() {
        _controller.stopIfRunning();
    }

    @Test
    public void failIfDnsRecordIsNotResolvedInTimeout() throws Exception {
        HashMap<String, String> envVars = new HashMap();
        envVars.put("WAIT_FOR_NS_RECORD", "example");
        envVars.put("WAIT_FOR_NS_RECORD_TIMEOUT", "10");

        try {
            _controller.start(envVars, false);
        }
        catch (ContainerLaunchException ex1) {
            // expected, the container fails to start
        }

        waitFor(Duration.ofSeconds(30), () -> _controller.getContainer().getLogsAnalyzer().contains("failing the container..."));
    }

    @Test
    public void waitForDnsRecordToBeResolved() throws Exception {
        HashMap<String, String> envVars = new HashMap();
        envVars.put("PORTAINER_ARGS", "--no-auth");
        envVars.put("WAIT_FOR_NS_RECORD", "example.docker");
        envVars.put("WAIT_FOR_NS_RECORD_TIMEOUT", "30");

        _controller.start(envVars, false);
        _controller.startDependencyContainer("example.docker");

        waitFor(Duration.ofSeconds(30), () -> _controller.getContainer().getLogsAnalyzer().contains("resolved successfully"));
    }
}