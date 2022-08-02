FROM clojure:openjdk-17-lein as build-env

ADD . /app
WORKDIR /app

RUN lein uberjar

# -----------------------------------------------------------------------------

from openjdk:17

RUN groupadd -r kvert && useradd -r -s /bin/false -g kvert kvert
RUN mkdir /app
COPY --from=build-env /app/target/uberjar/kvert-*-standalone.jar /app/kvert.jar

RUN chown -R kvert:kvert /app

user kvert

ENTRYPOINT ["java", "-jar", "/app/kvert.jar"]
