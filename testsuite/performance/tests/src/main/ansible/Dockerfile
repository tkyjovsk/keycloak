FROM centos:7
RUN yum update -y && yum -y install ansible openssh-clients && yum clean all
RUN groupadd -r ansible -g 1000 && useradd -u 1000 -r -g ansible -m -d /opt/ansible -s /sbin/nologin -c "AmsonAnsible user" ansible && \
    chmod 755 /opt/ansible && chown ansible /opt/ansible
WORKDIR /opt/ansible

ADD . ./

RUN chown -R ansible .; chgrp -R ansible .
RUN chmod 700 keys; chmod -R 600 keys/*
USER ansible

CMD ["ansible", "--version"]
