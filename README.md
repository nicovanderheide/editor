# editor
A locale properties editor

## codenvy factory (https://beta.codenvy.com/f?id=3tz2j3urktrvdlf1)

Download the executable jar:  
```bash.sh
wget http://ftp.nluug.nl/internet/apache/jackrabbit/2.13.2/jackrabbit-standalone-2.13.2.jar
```

To setup a 'local' jackrabbit webdav location the project can connect to use:
```bash.sh
java -jar jackrabbit-standalone-2.13.2.jar -p 18080
```

```bash.sh
mvn clean install test jetty:run
```
The jetty webserver will be available on: [localserver](http://localhost:8080/editor)
When running this in codenvy

usernames are: 
* 'ROLE_SUPER_ADMIN'
... This user is able to do all actions (CRUD)
* 'ROLE_ADMIN'
... This user is able to do the following actions (RU)
* 'ROLE_VIEWER'
... This user is able to perform all read actions

Password check isn't functional
