import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.ContainerLaunchException;

import java.util.HashMap;

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

        waitForMessageInStdOut("failing the container...", 15);
    }

    @Test
    public void waitForDnsRecordToBeResolved() throws Exception {
        HashMap<String, String> envVars = new HashMap();
        envVars.put("PORTAINER_ARGS", "--no-auth");
        envVars.put("WAIT_FOR_NS_RECORD", "example.docker");
        envVars.put("WAIT_FOR_NS_RECORD_TIMEOUT", "30");

        _controller.start(envVars, false);
        _controller.startDependencyContainer("example.docker");

        waitForMessageInStdOut("resolved successfully", 30);
    }

    private void waitForMessageInStdOut(String message, Integer timeout) throws Exception {
        long timeoutExpiredMs = System.currentTimeMillis() + (timeout * 1000);

        while(!_controller.getContainer().getLogs().contains(message)) {
            long waitMillis = timeoutExpiredMs - System.currentTimeMillis();

            if (waitMillis <= 0) {
                throw new Exception("The container output did not print the expected message \"" + message + "\" in time.");
            }

            Thread.sleep(1000);
        }
    }
}