FROM openjdk:11

ADD /build/natsStreamingClient-jar-with-dependencies.jar /opt/natsStreamingClient.jar

ENTRYPOINT ["java", "-jar", "/opt/natsStreamingClient.jar"]