FROM clojure:temurin-17-lein-focal as build-env

ADD . /app
WORKDIR /app

RUN lein uberjar

# -----------------------------------------------------------------------------

from eclipse-temurin:17-focal

RUN groupadd -r kvert && useradd -r -s /bin/false -g kvert kvert
RUN mkdir /app
COPY --from=build-env /app/target/uberjar/kvert-*-standalone.jar /app/kvert.jar

RUN chown -R kvert:kvert /app

user kvert

ENTRYPOINT ["java", "-jar", "/app/kvert.jar"]
