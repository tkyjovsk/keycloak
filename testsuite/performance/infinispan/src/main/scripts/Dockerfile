FROM registry.access.redhat.com/ubi8-minimal

ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV JSTAT false
ENV CONFIGURATION clustered.xml
ENV INFINISPAN_SERVER_HOME /opt/jboss/infinispan-server
WORKDIR $INFINISPAN_SERVER_HOME

USER root
RUN microdnf -y install iproute && microdnf clean all

ADD infinispan-server ./
ADD *.sh /usr/local/bin/
RUN chown -R 1000 .; chgrp -R 1000 .; chmod -R -v ug+x bin/*.sh ; chmod -R -v ug+x /usr/local/bin/ 

USER 1000
EXPOSE 7600 8080 8181 8888 9990 11211 11222 57600
HEALTHCHECK  --interval=5s --timeout=5s --retries=12 CMD ["infinispan-healthcheck.sh"]
ENTRYPOINT [ "docker-entrypoint-custom.sh" ]
