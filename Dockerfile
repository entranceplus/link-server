FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/links.jar /links/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/links/app.jar"]
