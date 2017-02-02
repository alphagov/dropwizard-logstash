package uk.gov.ida.dropwizard.logstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dropwizard.logstash.support.LogFormat;
import uk.gov.ida.dropwizard.logstash.support.TestApplication;
import uk.gov.ida.dropwizard.logstash.support.TestConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LogstashFileAppenderAppRuleTest {

    private static final String LOGSTASH_FILE_REQUESTS_LOG = "./build/logstash-file-requests.log";
    private static final String LOGSTASH_FILE_LOG_LOG = "./build/logstash-file-log.log";
    private static File requestLog;
    private static File logLog;

    // this is executed before the @ClassRule
    static {
        requestLog = new File(LOGSTASH_FILE_REQUESTS_LOG);
        logLog = new File(LOGSTASH_FILE_LOG_LOG);
        // delete the files
        requestLog.delete();
        logLog.delete();
    }

    @ClassRule
    public static DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule(TestApplication.class, ResourceHelpers.resourceFilePath("test-application.yml"));

    @Before
    public void before() {
        assertThat(requestLog.exists()).isTrue();
        assertThat(logLog.exists()).isTrue();
    }

    @Test
    public void testLoggingLogstashRequestLog() throws InterruptedException, IOException {
        Client client = new JerseyClientBuilder().build();

        final Response response = client.target("http://localhost:" + dropwizardAppRule.getLocalPort() + "/").request().get();

        assertThat(response.readEntity(String.class)).isEqualTo("hello!");

        assertThat(requestLog.length()).isGreaterThan(0);

        final List<LogFormat> list = parseLog(requestLog);

        assertThat(list.size()).isEqualTo(1);

        // this is currently returning the host, like this: "GET //localhost:63932/ HTTP/1.1" 200
//        assertThat(list.get(0).getMessage()).contains("\"GET / HTTP/1.1\" 200");
        assertThat(list.get(0).getLoggerName()).isEqualTo("http.request");

    }

    @Test
    public void testLoggingLogstashFileLog() throws IOException {

        final List<LogFormat> list = parseLog(logLog);

        assertThat(list.size()).isGreaterThan(0);

        assertThat(list.stream()
                .filter(logFormat -> logFormat.getMessage().equals("The following paths were found for the configured resources:\n\n    GET     / (uk.gov.ida.dropwizard.logstash.support.RootResource)\n"))
                .count()).isEqualTo(1);
    }

    private List<LogFormat> parseLog(File logLog) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Files.readAllLines(logLog.toPath()).stream()
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, LogFormat.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(object -> object != null)
                .collect(Collectors.toList());
    }
}
