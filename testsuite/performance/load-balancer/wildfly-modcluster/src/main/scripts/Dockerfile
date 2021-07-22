FROM registry.access.redhat.com/ubi8-minimal

ENV JBOSS_HOME /opt/jboss/wildfly
WORKDIR $JBOSS_HOME

ENV CONFIGURATION standalone.xml

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV JSTAT false

USER root
RUN microdnf update -y && microdnf install -y iproute glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all

ADD wildfly ./
ADD *.sh /usr/local/bin/

RUN chown -R 1000 . ;\
    chgrp -R 1000 . ;\
    chmod -R -v ug+x bin/*.sh ;\
    chmod -R -v ug+x /usr/local/bin/ 

USER 1000

EXPOSE 8080
EXPOSE 9990
HEALTHCHECK  --interval=5s --timeout=5s --retries=12 CMD ["wildfly-healthcheck.sh"]
ENTRYPOINT ["docker-entrypoint.sh"]
