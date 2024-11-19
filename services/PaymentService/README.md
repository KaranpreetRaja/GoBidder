# Payment Service

This service is responsible for handling payments.

## Running With Docker

To run this service in a Docker container (from this directory), you can use

```shell
docker build -t payment-service .
```

```shell
docker run -it --rm -p 8081:8081 payment-service
```

You can also use Docker Compose from the parent directory.

```shell
docker compose up payment-service -d
```

## Running Locally

This is a Spring Boot application, so you can run this easily from IntelliJ or
however you're comfortable running Spring Boot servers.

If you need to use the command line, and you have Maven installed, you can use

```shell
mvn clean package
```

to build the JAR.

If you don't have Maven installed, then on UNIX you use

```shell
./mvn clean package
```

and on Windows you can use

```shell
./mvnw.cmd clean package
```

The JAR file will be called `payment-<VERSION>.jar`, where `<VERSION>` is the
version from the [pom.xml](./pom.xml) file.

From here, you can run it like a normal JAR file

```shell
java -jar payment-<VERSION>.jar
```
