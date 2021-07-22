FROM registry.access.redhat.com/ubi8-minimal

ENV JBOSS_HOME /opt/jboss/keycloak
WORKDIR $JBOSS_HOME

ENV CONFIGURATION standalone.xml

# Enables signals getting passed from startup script to JVM
# ensuring clean shutdown when container is stopped.
ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV PROXY_ADDRESS_FORWARDING false
ENV JSTAT false

USER root
RUN microdnf update -y && microdnf install -y iproute glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all
#RUN yum install -y epel-release jq iproute && yum clean all

ADD keycloak ./
ADD *.sh /usr/local/bin/

RUN chown -R 1000 .; chgrp -R 1000 .; chmod -R -v ug+x bin/*.sh ; \
    chmod -R -v ug+x /usr/local/bin/ 

USER 1000

EXPOSE 8080
EXPOSE 9990
HEALTHCHECK  --interval=5s --timeout=5s --retries=12 CMD ["keycloak-healthcheck.sh"]
ENTRYPOINT ["docker-entrypoint.sh"]