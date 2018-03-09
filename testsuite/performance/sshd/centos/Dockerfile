FROM centos:7
ADD install-sshd.sh .
RUN ./install-sshd.sh
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
