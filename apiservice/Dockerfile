
FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app

# Kopiujemy plik JAR (zakładam, że używasz Mavena i nazwa to apiservice-0.0.1-SNAPSHOT.jar)
# Możesz użyć gwiazdki, aby Docker sam znalazł plik
COPY build/libs/*-SNAPSHOT.jar app.jar

# Port aplikacji
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]