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
    rhc app create $APP_NAME diy-0.1 mysql-5.5 > $TMP/rhc.log
    cat $TMP/rhc.log |grep -e SSH|grep -oE "[a-z0-9@.-]*$" > $CREDENTIALS_FILE
    rhc cartridge add "http://cartreflect-claytondev.rhcloud.com/reflect?github=adelolmo/openshift-redis-cart" -a $APP_NAME > /dev/null 2>&1

    IN=$(cat $CREDENTIALS_FILE);
    arrIN=(${IN//@/ })
    ssh_username=${arrIN[0]}
    ssh_host=${arrIN[1]}
    ssh_cmd=$ssh_username@$ssh_host
    remote_home=$OPENSHIFT_DIR/$ssh_username
    openshift_diy_dir=$OPENSHIFT_DIR/$ssh_username/$DIY

    echo remote_home=$remote_home
    echo openshift_diy_dir=$openshift_diy_dir

    echo "> stop ruby service"
    ssh $ssh_cmd pkill ruby

    echo "> replace gear action_hooks start"
    echo "$openshift_diy_dir/.openshift/action_hooks/start" > $TMP/openshift_start
    scp $TMP/openshift_start $ssh_cmd:$remote_home/app-root/repo/.openshift/action_hooks/start
    rm $TMP/openshift_start
    ;;

deploy)
    if [ ! -f $CREDENTIALS_FILE ]; then
        echo "Error! Missing credentials file \`$CREDENTIALS_FILE'.";
        echo "File should contain: rhc_ssh_username@rhc_ssh_host";
        exit 1;
    fi

    IN=$(cat $CREDENTIALS_FILE);
    arrIN=(${IN//@/ })
    ssh_username=${arrIN[0]}
    ssh_host=${arrIN[1]}
    ssh_command=$ssh_username@$ssh_host
    remote_home=$OPENSHIFT_DIR/$ssh_username

    echo "======================"
    echo "Deploying to OpenShift"
    echo "======================"
    echo
    echo "Host: $ssh_host"
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
    scp $TMP/version.txt $ssh_command:$remote_home/$DIY

    echo "> copying scripts"
    scp -r .openshift $ssh_command:$remote_home/$DIY

    echo "> deploying artifact"
    cp target/biblio-server*.jar $TMP/biblio-server.jar
    scp $TMP/biblio-server.jar $ssh_command:$remote_home/$DIY

    echo "> restart application"
    ssh $ssh_command "$remote_home/$DIY/.openshift/action_hooks/stop"
    ssh $ssh_command "$remote_home/$DIY/.openshift/action_hooks/deploy"
    ssh $ssh_command "$remote_home/$DIY/.openshift/action_hooks/start"

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