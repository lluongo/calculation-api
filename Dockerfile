FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get upgrade -y && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/calculation-api*.jar calculation-api.jar
RUN chmod 755 /app/calculation-api.jar
EXPOSE 8090
RUN adduser --system --group --uid 1001 appuser
USER appuser
CMD ["java", "-jar", "/app/calculation-api.jar"]