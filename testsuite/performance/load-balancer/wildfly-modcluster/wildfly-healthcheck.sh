#!/bin/bash

$JBOSS_HOME/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" | grep -q "running"
