import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class PortainerContainerShould extends ContainerTestBase {
    private static final Logger logger = LoggerFactory.getLogger(ContainerTestBase.class);

    @Test
    public void respondOnWebUiPort() throws IOException {
        URL root = new URL(String.format("http://%s:%d",
                getContainer().getContainerIpAddress(),
                getContainer().getMappedPort(9000)));

        HttpURLConnection connection = (HttpURLConnection)root.openConnection();
        connection.connect();

        assertEquals(200,  connection.getResponseCode());
    }
}