FROM amazoncorretto:17-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:17-alpine AS production
ENV MAIL_SERVER_HOST smtp.gmail.com
ENV MAIL_SERVER_PORT 587
ENV PORT 8080
ENV PUBLIC_ADDRESS http://localhost:8080
VOLUME /tmp
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE $PORT
CMD ["java", "-jar", "app.jar"]
