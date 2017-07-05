package uk.gov.ida.dropwizard.logstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import uk.gov.ida.dropwizard.logstash.support.LoggingEventFormat;
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

    public static DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule<>(TestApplication.class, ResourceHelpers.resourceFilePath("console-appender-test-application.yml"));
    public static SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @ClassRule
    public static TestRule ruleChain = RuleChain
            .outerRule(systemOutRule)
            .around(dropwizardAppRule);

    @Test
    public void testLoggingLogstashRequestLog() throws InterruptedException, IOException {
        Client client = new JerseyClientBuilder().build();

        final Response response = client.target("http://localhost:" + dropwizardAppRule.getLocalPort() + "/").request().get();

        assertThat(response.readEntity(String.class)).isEqualTo("hello!");

        final List<LoggingEventFormat> list = parseLog();

        assertThat(list.stream().filter(logLine -> logLine.getLoggerName().equals("http.request")).count()).isEqualTo(1);

    }

    @Test
    public void testLoggingLogstashFileLog() throws IOException {

        final List<LoggingEventFormat> list = parseLog();

        assertThat(list.size()).isGreaterThan(0);

        assertThat(list.stream()
                .filter(logFormat -> logFormat.getMessage().equals("The following paths were found for the configured resources:\n\n    GET     / (uk.gov.ida.dropwizard.logstash.support.RootResource)\n"))
                .count()).isEqualTo(1);
    }

    private List<LoggingEventFormat> parseLog() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LoggingEventFormat> list = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(systemOutRule.getLog(), System.lineSeparator());
        while(stringTokenizer.hasMoreTokens()) {
            list.add(objectMapper.readValue(stringTokenizer.nextToken(), LoggingEventFormat.class));
        }
        return list;
    }
}
