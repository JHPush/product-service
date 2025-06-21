# Stage 1: Build and extract Spring Boot layers
FROM openjdk:17-slim AS build
WORKDIR /application

# 빌드된 JAR 경로 설정
ARG JAR_FILE=build/libs/product-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar

# Spring Boot layered JAR 추출
RUN java -Djarmode=layertools -jar application.jar extract

# Stage 2: Final image with layers
FROM openjdk:17-slim
WORKDIR /application

# Spring Boot layers 복사
COPY --from=build /application/dependencies/ ./dependencies/
COPY --from=build /application/spring-boot-loader/ ./spring-boot-loader/
COPY --from=build /application/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=build /application/application/ ./application/

# ✅ CSV 리소스 파일 복사 (resources/data/products.csv 포함)
COPY src/main/resources/data/ /application/application/resources/data/

# 실행 설정
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]