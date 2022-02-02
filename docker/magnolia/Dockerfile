FROM tomcat:9.0.38-jdk11-openjdk-slim

ENV JAVA_OPTS="-Dmagnolia.home=/opt/magnolia -Dmagnolia.update.auto=true -Dmagnolia.author.key.location=/opt/magnolia/activation-key/magnolia-activation-keypair.properties -Dmagnolia.develop=true -Dmagnolia.bootstrap.authorInstance=true"

# Copy Tomcat setenv.sh
#COPY docker/files/tomcat-setenv.sh $CATALINA_HOME/bin/setenv.sh
#RUN chmod +x $CATALINA_HOME/bin/setenv.sh

# This directory is used for Magnolia property "magnolia.home"
RUN mkdir /opt/magnolia
RUN chmod 755 /opt/magnolia

RUN rm -rf $CATALINA_HOME/webapps/*
COPY magnolia-tokenizer-bundle-webapp/target/ROOT.war $CATALINA_HOME/webapps/ROOT.war

EXPOSE 8080