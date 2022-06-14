FROM maven:3.8.5-openjdk-17-slim
EXPOSE 8080
COPY . /app/
WORKDIR /app
RUN mvn clean package -DskipTests
ENTRYPOINT echo java -jar target/SocialNetwork-0.0.1-SNAPSHOT.jar; exec java -jar target/SocialNetwork-0.0.1-SNAPSHOT.jar