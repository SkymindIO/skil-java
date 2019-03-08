package ai.skymind;

import ai.skymind.skil.DefaultApi;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SkilTest {

    private String userId = "admin";
    private String password = "admin";
    private String host = "localhost";
    private int port = 9008;

    @Before
    public void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("/app.properties").getInputStream());
        userId = properties.getProperty("default.userId") == null ? userId : properties.getProperty("default.userId");
        password = properties.getProperty("default.password") == null ? password : properties.getProperty("default.password");
        host = properties.getProperty("default.host") == null ? host : properties.getProperty("default.host");
        port = properties.getProperty("default.port") == null ? port : Integer.valueOf(properties.getProperty("default.port"));
    }


    @Test
    public void testCreation() throws Exception {
        Skil skil = new Skil();
        assert ! skil.getDefaultServerId().isEmpty();
    }

    @Test
    public void testCreationPass() throws Exception {
        Skil skil = new Skil(host, port, userId, password, false);
        assertFalse(skil.getDefaultServerId().isEmpty());

        assertTrue(skil.getHost().equals(host));
        assertTrue(skil.getPort() == port);
        DefaultApi api = skil.getApi();
    }
}
