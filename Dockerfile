FROM amazoncorretto:17-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY entrypoint.sh /
COPY spring.env /
RUN mkdir -p  img/detailImages
RUN mkdir -p img/thumbnail
RUN chmod -R 775 img
RUN chmod +x /entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
