FROM jboss/base-jdk:8

ENV JBOSS_HOME /opt/jboss/keycloak
WORKDIR $JBOSS_HOME

ENV CONFIGURATION standalone.xml

# Enables signals getting passed from startup script to JVM
# ensuring clean shutdown when container is stopped.
ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV PROXY_ADDRESS_FORWARDING false

ADD target/keycloak configs/ ./
ADD *.sh /usr/local/bin/

USER root
RUN chown -R jboss .; chgrp -R jboss .; 
RUN chmod -R -v +x /usr/local/bin/
RUN yum install -y epel-release jq iproute && yum clean all

USER jboss
# install mariadb JDBC driver
RUN mkdir -p modules/system/layers/base/org/mariadb/jdbc/main; \
    cd modules/system/layers/base/org/mariadb/jdbc/main; \
    curl -O http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/2.0.3/mariadb-java-client-2.0.3.jar
ADD module.xml modules/system/layers/base/org/mariadb/jdbc/main/
# apply configurations
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=set-keycloak-ds.cli
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=io-worker-threads.cli
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=undertow.cli
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=distributed-cache-owners.cli
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=modcluster-simple-load-provider.cli; \
    cd $JBOSS_HOME/standalone; rm -rf configuration/standalone_xml_history log data tmp

EXPOSE 8080
HEALTHCHECK  --interval=5s --timeout=5s --retries=12 CMD ["keycloak-healthcheck.sh"]
ENTRYPOINT ["docker-entrypoint.sh"]
