package uk.gov.ida.dropwizard.logstash.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessEventFormat {

    @JsonProperty("@timestamp")
    public String timestamp;

    @JsonProperty("@version")
    public int version;

    public int content_length;
    public int elapsed_time;
    public String hostname;
    public String method;
    public String protocol;
    public String remote_host;
    public String remote_user;
    public String requested_uri;
    public int status_code;
    public String message;

    private AccessEventFormat() {

    }

    public AccessEventFormat(String timestamp, int version) {
        this.timestamp = timestamp;
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public int getContentLength() {
        return content_length;
    }

    public int getElapsedTime() {
        return elapsed_time;
    }

    public String getHostname() {
        return hostname;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRemoteHost() {
        return remote_host;
    }

    public String getRemoteUser() {
        return remote_user;
    }

    public String getRequestedUri() {
        return requested_uri;
    }

    public int getStatusCode() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }
}
