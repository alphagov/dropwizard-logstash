package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.encoder.LogstashAccessEncoder;
import net.logstash.logback.fieldnames.LogstashAccessFieldNames;

@JsonTypeName("access-logstash-console")
public class AccessLogstashConsoleAppenderFactory extends ConsoleAppenderFactory<IAccessEvent> {
    /**
     * Resets the field names to be modern (post-2013) logstash style
     * @see <a href="https://logstash.jira.com/browse/LOGSTASH-675">the logstash issue changing the name scheme</a>
     */
    private static final LogstashAccessFieldNames logstash675FieldNames = new LogstashAccessFieldNames() {{
        setFieldsContentLength("content_length");
        setFieldsElapsedTime("elapsed_time");
        setFieldsHostname("hostname");
        setFieldsMethod("method");
        setFieldsProtocol("protocol");
        setFieldsRemoteHost("remote_host");
        setFieldsRemoteUser("remote_user");
        setFieldsRequestedUri("requested_uri");
        setFieldsRequestedUrl(null); // duplicates requested_uri, method, protocol
        setFieldsStatusCode("status_code");
        setMessage(MessageJsonProvider.FIELD_MESSAGE);
    }};

    @Override
    public Appender<IAccessEvent> build(LoggerContext context,
                                         String applicationName,
                                         LayoutFactory<IAccessEvent> layout,
                                         LevelFilterFactory<IAccessEvent> levelFilterFactory,
                                         AsyncAppenderFactory<IAccessEvent> asyncAppenderFactory) {

        LogstashAccessEncoder encoder = new LogstashAccessEncoder();
        encoder.setFieldNames(logstash675FieldNames);
        encoder.setContext(context);
        encoder.start();

        final ConsoleAppender<IAccessEvent> appender = new ConsoleAppender<>();
        appender.setName("access-logstash-console-appender");
        appender.setContext(context);
        appender.setTarget(getTarget().get());
        appender.setEncoder(encoder);
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }
}
