FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN chmod +x ./mvnw 2>/dev/null || true

RUN if [ -f "./mvnw" ]; then ./mvnw clean package -DskipTests; else apt-get update && apt-get install -y maven && mvn clean package -DskipTests; fi

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]
