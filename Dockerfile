# Этап сборки (build)
FROM amazoncorretto:21-alpine AS build

WORKDIR /app

# Копируем Maven-файлы
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw

# Скачиваем зависимости в кэш
RUN ./mvnw dependency:go-offline

# Теперь копируем исходники и собираем
COPY src src
RUN ./mvnw clean package -DskipTests

# Этап выполнения (runtime)
FROM amazoncorretto:21-alpine

WORKDIR /app

# Копируем собранный JAR-файл из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]