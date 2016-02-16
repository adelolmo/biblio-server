#!/bin/bash

APP_NAME=bibliorest
DIY=diy
OPENSHIFT_DIR=/var/lib/openshift
CREDENTIALS_FILE=.openshift.credentials
TMP=/tmp/biblio

OPTION="$1"

case $OPTION in
create)
    echo "======================="
    echo "Create OpenShift server"
    echo "======================="
    echo
    echo "Application name: $APP_NAME"
    echo

    if [ -z `which rhc` ]; then
        echo "Error! Missing rhc command";
        echo "Visit https://github.com/openshift/rhc for installation details";
        exit 1;
    fi
    rhc app create $APP_NAME diy-0.1 mysql-5.5 > /tmp/rhc.log
    cat /tmp/rhc.log |grep -e SSH|grep -oE "[a-z0-9@.-]*$" > $CREDENTIALS_FILE
    rhc cartridge add "http://cartreflect-claytondev.rhcloud.com/reflect?github=adelolmo/openshift-redis-cart" -a $APP_NAME > /dev/null 2>&1

    IN=$(cat $CREDENTIALS_FILE);
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
    if [ ! -f $CREDENTIALS_FILE ]; then
        echo "Error! Missing credentials file \`$CREDENTIALS_FILE'.";
        echo "File should contain: rhc_ssh_username@rhc_ssh_host";
        exit 1;
    fi

    IN=$(cat $CREDENTIALS_FILE);
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

    echo "> setup"
    rm -rf $TMP
    mkdir -p $TMP

    echo "> build artifact"
    mvn -f ../pom.xml clean package > /dev/null 2>&1

    echo "> generate version"
    version=$(ls target/biblio-server*.jar|grep -oE "([0-9]\.[0-9]+)(-SNAPSHOT)?")
    datetime=$(date +"%d.%m.%Y %T")
    echo "$version ($datetime)" > $TMP/version.txt
    echo "  Version: $version ($datetime)"
    scp $TMP/version.txt $SSH_CMD:$REMOTE_HOME/$DIY

    echo "> copying scripts"
    scp -r .openshift $SSH_CMD:$REMOTE_HOME/$DIY

    echo "> deploying artifact"
    cp target/biblio-server*.jar $TMP/biblio-server.jar
    scp $TMP/biblio-server.jar $SSH_CMD:$REMOTE_HOME/$DIY

    echo "> restart application"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/stop"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/deploy"
    ssh $SSH_CMD "$REMOTE_HOME/$DIY/.openshift/action_hooks/start"

    echo "> cleanup"
    rm -rf $TMP

    echo "done"
    ;;

*)
    echo
    echo "Usage: $0 (create|deploy)"
    exit 1

esac

exit 0