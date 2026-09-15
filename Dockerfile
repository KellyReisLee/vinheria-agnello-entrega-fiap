# --- ETAPA 1: Compilar o projeto com Maven ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- ETAPA 2: Rodar a aplicação no Tomcat ---
FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o arquivo .war gerado na etapa anterior para o Tomcat com o nome ROOT.war
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]