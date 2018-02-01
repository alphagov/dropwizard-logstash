# dropwizard-logstash

Dropwizard extension that supports logstash format with various appenders: `logstash-file`, `logstash-syslog`, `logstash-console`

## Using the library

* Build and install to your local repository: `./gradlew publishToMavenLocal`
* Add the dependency to your project: `uk.gov.ida:dropwizard-logstash:1.0.5-SNAPSHOT`
* Add the bundle to your app: `bootstrap.addBundle(new LogstashBundle())`
* Configure the logger in the application config file by using `logstash-file`, `logstash-syslog`, or `logstash-console`: 
````yaml
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
* There is also a logback-access native console appender, `access-logstash-console`:
````yaml
server:
    requestLog:
        appenders:
            - type: access-logstash-console

````
           

### Running the test suite

`./pre-commit.sh`

### Recreating the Jenkins build environment with docker

To reproduce exactly what the Jenkins build server does, with docker,
use the commands below to build the package and run the tests inside it.
This is useful for re-creating and debugging build failures.

```bash
docker run -it -v "$(pwd)":/app -w /app --rm govukverify/java8:latest clean build test
```

Connect to the container to poke around with 
````bash
docker run govukverify/java8:latest /bin/bash.
````

## Licence

[MIT License](LICENCE)

## Versioning policy

dropwizard-logstash-[dropwizard version]-[build number]

