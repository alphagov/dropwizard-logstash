# dropwizard-logstash

Dropwizard extension that supports logstash format with various appenders: `logstash-file`, `logstash-syslog`, `logstash-console`

## Using the library

* Build and install to your local repository: `gradle publishToMavenLocal`
* Add the dependency to your project: `uk.gov.ida:dropwizard-logstash:1.0.5-SNAPSHOT`
* Add the bundle to your app: `bootstrap.addBundle(new LogstashBundle())`
* Configure the logger in the application config file by using `logstash-file`, `logstash-syslog`, or `logstash-console`: 
````
server:
    requestLog:
        type: classic
        appenders:
            - type: logstash-file
              currentLogFilename: app.log
              archivedLogFilenamePattern: app.log.%d.gz
              archivedFileCount: x

logging:
    appenders:
        - type: logstash-file
          currentLogFilename: app.log
          archivedLogFilenamePattern: app.log.%d.gz
          archivedFileCount: x    
````
           

### Running the test suite

`./pre-commit.sh`

## Licence

[MIT License](LICENCE)

## Versioning policy

dropwizard-logstash-[dropwizard version]-[build number]

