FROM amazoncorretto:17-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:17-alpine AS production
ENV MAIL_SERVER_HOST smtp.gmail.com
ENV MAIL_SERVER_PORT 587
ENV PORT 443
ENV PUBLIC_HOST_ADDRESS localhost
ENV SHOULD_INCLUDE_SERVER_PORT true
VOLUME /tmp
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
