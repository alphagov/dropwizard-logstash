package uk.gov.ida.dropwizard.logstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dropwizard.logstash.support.AccessEventFormat;
import uk.gov.ida.dropwizard.logstash.support.TestApplication;
import uk.gov.ida.dropwizard.logstash.support.TestConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class AccessLogstashConsoleAppenderAppRuleTest {

    private static ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    // this is executed before the @ClassRule
    static {
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @ClassRule
    public static DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule<>(TestApplication.class, ResourceHelpers.resourceFilePath("access-console-appender-test-application.yml"));

    @Test
    public void testLoggingLogstashRequestLog() throws InterruptedException, IOException {
        Client client = new JerseyClientBuilder().build();

        final Response response = client.target("http://localhost:" + dropwizardAppRule.getLocalPort() + "/").request().get();

        assertThat(response.readEntity(String.class)).isEqualTo("hello!");

        final List<AccessEventFormat> list = parseLog();

        List<AccessEventFormat> accessEventStream = list.stream().filter(accessLog -> accessLog.getMethod().equals("GET")).collect(toList());
        assertThat(accessEventStream.size()).isEqualTo(1);
        AccessEventFormat accessEvent = accessEventStream.get(0);
        assertThat(accessEvent.getMethod()).isEqualTo("GET");
        assertThat(accessEvent.getContentLength()).isEqualTo(6);
        assertThat(accessEvent.getRequestedUri()).isEqualTo("/");
        assertThat(accessEvent.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(accessEvent.getStatusCode()).isEqualTo(200);
        assertThat(accessEvent.getVersion()).isEqualTo(1);
    }

    private List<AccessEventFormat> parseLog() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessEventFormat> list = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(byteArrayOutputStream.toString(), System.lineSeparator());
        while(stringTokenizer.hasMoreTokens()) {
            list.add(objectMapper.readValue(stringTokenizer.nextToken(), AccessEventFormat.class));
        }
        return list;
    }
}
