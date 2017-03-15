package uk.gov.ida.dropwizard.logstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dropwizard.logstash.support.LogFormat;
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

import static org.assertj.core.api.Assertions.assertThat;

public class LogstashConsoleAppenderAppRuleTest {

    private static ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    // this is executed before the @ClassRule
    static {
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @ClassRule
    public static DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule(TestApplication.class, ResourceHelpers.resourceFilePath("console-appender-test-application.yml"));

    @Test
    public void testLoggingLogstashRequestLog() throws InterruptedException, IOException {
        Client client = new JerseyClientBuilder().build();

        final Response response = client.target("http://localhost:" + dropwizardAppRule.getLocalPort() + "/").request().get();

        assertThat(response.readEntity(String.class)).isEqualTo("hello!");

        final List<LogFormat> list = parseLog();

        assertThat(list.stream().filter(logLine -> logLine.getLoggerName().equals("http.request")).count()).isEqualTo(1);

    }

    @Test
    public void testLoggingLogstashFileLog() throws IOException {

        final List<LogFormat> list = parseLog();

        assertThat(list.size()).isGreaterThan(0);

        assertThat(list.stream()
                .filter(logFormat -> logFormat.getMessage().equals("The following paths were found for the configured resources:\n\n    GET     / (uk.gov.ida.dropwizard.logstash.support.RootResource)\n"))
                .count()).isEqualTo(1);
    }

    private List<LogFormat> parseLog() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LogFormat> list = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(byteArrayOutputStream.toString(), System.lineSeparator());
        while(stringTokenizer.hasMoreTokens()) {
            list.add(objectMapper.readValue(stringTokenizer.nextToken(), LogFormat.class));
        }
        return list;
    }
}
