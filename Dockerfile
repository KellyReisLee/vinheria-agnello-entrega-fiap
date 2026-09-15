# Usa uma imagem oficial do Tomcat com Java
FROM tomcat:10.1-jdk17

# Remove os aplicativos padrão que vêm no Tomcat para liberar espaço/portas
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o código ou o arquivo war gerado para a pasta raiz do Tomcat renomeando para ROOT
# (Isso garante que o projeto abra direto na URL principal do site)
COPY target/vinheria-agnello.war /usr/local/tomcat/webapps/ROOT.war

# Expõe a porta padrão que o Tomcat usa
EXPOSE 8080

# Inicia o Tomcat
CMD ["catalina.sh", "run"]