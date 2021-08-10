import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PortainerContainerShould {
    private static final ContainerController Controller = new ContainerController();

    @BeforeClass
    public static void beforeClass() {
        Controller.start(new HashMap(), true);
    }

    @AfterClass
    public static void afterClass() {
        Controller.stopIfRunning();
    }

    @Test
    public void respondOnWebUiPort() throws IOException {
        URL root = new URL(String.format("http://%s:%d",
                Controller.getContainer().getContainerIpAddress(),
                Controller.getContainer().getMappedPort(9000)));

        HttpURLConnection connection = (HttpURLConnection)root.openConnection();
        connection.connect();

        assertEquals(200,  connection.getResponseCode());
    }
}