FROM clojure:openjdk-17-lein as build-env

ADD . /app
WORKDIR /app

RUN lein uberjar

# -----------------------------------------------------------------------------

from openjdk:17

RUN groupadd -r ymlgen && useradd -r -s /bin/false -g ymlgen ymlgen
RUN mkdir /app
COPY --from=build-env /app/target/uberjar/ymlgen-*-standalone.jar /app/ymlgen.jar

RUN chown -R ymlgen:ymlgen /app

user ymlgen

ENTRYPOINT ["java", "-jar", "/app/ymlgen.jar"]
