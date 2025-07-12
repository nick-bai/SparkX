# 使用简化版
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
# 从构建阶段复制生成的JAR包（根据实际路径调整）
COPY sparkx-web-*.jar ./sparkx-web.jar

ENTRYPOINT ["/bin/sh", "-c", "java -jar sparkx-web.jar"]