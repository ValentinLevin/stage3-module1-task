FROM bellsoft/liberica-openjdk-alpine-musl:17-cds AS builder
WORKDIR /app
COPY ./gradlew ./
COPY ./gradle ./gradle
COPY ./*.gradle ./

RUN ./gradlew dependencies

COPY ./module-repository ./module-repository
COPY ./module-service ./module-service
COPY ./module-web ./module-web
COPY ./module-main ./module-main

RUN ./gradlew build

FROM bellsoft/liberica-openjre-alpine-musl:17-cds
WORKDIR app
COPY --from=builder /app/module-main/build/libs/module-main-*-all.jar ./news-management.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "./news-management.jar"]