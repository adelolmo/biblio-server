#!/bin/bash

APP_NAME=bibliorest
DIY=diy
OPENSHIFT_DIR=/var/lib/openshift

OPTION="$1"

case $OPTION in
create)
    echo "======================="
    echo "Create OpenShift server"
    echo "======================="
    echo
    echo "Application name: $APP_NAME"
    echo

    rhc app create $APP_NAME diy-0.1 mysql-5.5 > /tmp/rhc.log
    cat /tmp/rhc.log |grep -e SSH|grep -oE "[a-z0-9@.-]*$" > .openshift.credentials
    rhc cartridge add "http://cartreflect-claytondev.rhcloud.com/reflect?github=smarterclayton/openshift-redis-cart" -a $APP_NAME 2>&1 >/dev/null

    IN=`cat .openshift.credentials`
    arrIN=(${IN//@/ })
    SSH_USERNAME=${arrIN[0]}
    SSH_HOST=${arrIN[1]}
    SSH_CMD=$SSH_USERNAME@$SSH_HOST
    REMOTE_HOME=$OPENSHIFT_DIR/$SSH_USERNAME
    OPENSHIFT_DIY_DIR=$OPENSHIFT_DIR/$SSH_USERNAME/$DIY

    echo REMOTE_HOME=$REMOTE_HOME
    echo OPENSHIFT_DIY_DIR=$OPENSHIFT_DIY_DIR

    echo "> stop ruby service"
    ssh $SSH_CMD pkill ruby

    echo "> replace gear action_hooks start"
    echo "$OPENSHIFT_DIY_DIR/.openshift/action_hooks/start" > /tmp/openshift_start
    scp /tmp/openshift_start $SSH_CMD:$REMOTE_HOME/app-root/repo/.openshift/action_hooks/start
    rm /tmp/openshift_start
    ;;

deploy)
    IN=`cat .openshift.credentials`
    arrIN=(${IN//@/ })
    SSH_USERNAME=${arrIN[0]}
    SSH_HOST=${arrIN[1]}
    SSH_CMD=$SSH_USERNAME@$SSH_HOST
    REMOTE_HOME=$OPENSHIFT_DIR/$SSH_USERNAME

    echo "======================"
    echo "Deploying to OpenShift"
    echo "======================"
    echo
    echo "Host: $SSH_HOST"
    echo

    echo "> build artifact"
    mvn -f ../pom.xml clean package 2>&1 >/dev/null

    echo "> generate version.txt"
    ls target/biblio-server*.jar|grep -oE "([0-9]\.[0-9]+)(-SNAPSHOT)?"> /tmp/version.txt
    scp /tmp/version.txt $SSH_CMD:$REMOTE_HOME/$DIY
    echo "Version: `cat /tmp/version.txt`"
    rm /tmp/version.txt

    echo "> copying scripts"
    scp -r .openshift $SSH_CMD:$REMOTE_HOME/$DIY

    echo "> deploying artifact"
    mkdir -p /tmp/biblio-server
    cp target/biblio-server*.jar /tmp/biblio-server/biblio-server.jar
    scp /tmp/biblio-server/biblio-server.jar $SSH_CMD:$REMOTE_HOME/$DIY

    echo "> restart application"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/stop"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/deploy"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/start"

    echo "done"
    ;;

*)
    echo
    echo "Usage: $0 (create|deploy)"
    exit 1

esac

exit 0