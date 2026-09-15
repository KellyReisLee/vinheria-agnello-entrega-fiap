FROM tomcat:10.1-jdk17

# Remove aplicações padrão do Tomcat para limpar o ambiente
RUN rm -rf /usr/local/tomcat/webapps/*

# Define a flag do Java para preferir IPv4 e evitar falhas de rede no Railway
ENV CATALINA_OPTS="-Djava.net.preferIPv4Stack=true"

# Copia o seu WAR gerado para o Tomcat (ajuste o caminho se o seu arquivo war tiver outro nome)
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]