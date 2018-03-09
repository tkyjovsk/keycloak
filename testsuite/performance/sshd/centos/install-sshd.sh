#!/bin/bash
yum -y install openssh-server
mkdir /var/run/sshd
echo 'root:provision' | chpasswd
sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
ENV NOTVISIBLE "in users profile"
echo "export VISIBLE=now" >> /etc/profile

# configure host key
mkdir -p /etc/ssh
ssh-keygen -b 2048 -t rsa -f /etc/ssh/ssh_host_rsa_key -q -N ""
chmod 600 /etc/ssh/ssh_host_rsa_key
