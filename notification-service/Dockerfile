# Estágio 1: Build (A "Oficina")
# Usa a imagem do Gradle com JDK 21 para compilar o projeto
FROM gradle:8.5.0-jdk21-alpine AS build

# Define o diretório de trabalho
WORKDIR /home/gradle/src

# Copia todo o código-fonte do projeto para o container
COPY --chown=gradle:gradle . .

# Executa o build do Gradle.
RUN gradle build -x test --no-daemon --stacktrace

# Estágio 2: Run (A "Aplicação Final")
# Usa uma imagem leve contendo apenas o Java 21 Runtime
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o arquivo .jar compilado do estágio 'build'
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expõe a porta que o Spring Boot usa (definida no application.properties)
# Lembre-se que definimos como 8081 no .properties
EXPOSE 8081

# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]