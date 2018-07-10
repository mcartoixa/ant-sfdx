#!/bin/bash

_ANT_VERSION=1.9.12
_PMD_VERSION=6.5.0

if [ -f ~/.bashrc ]; then
    . ~/.bashrc;
fi
if [ -f ./.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 -r)
    grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 -r printf "\$%s\n"
    echo
fi

echo \$JAVA_HOME=$JAVA_HOME

if [ ! -d .tmp ]; then mkdir .tmp; fi

#Ant
if [ ! -f .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz ]; then
    wget -nv -O .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz http://apache.mirrors.ovh.net/ftp.apache.org/dist//ant/binaries/apache-ant-$_ANT_VERSION-bin.tar.gz
    tar -xzvf .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz -C .tmp
fi
export ANT_HOME=$(pwd)/.tmp/apache-ant-$_ANT_VERSION
echo \$ANT_HOME=$ANT_HOME

# PMD
# Best would to be able to manage PMD with Apache Ivy but this looks like an impossible task...
if [ ! -f .tmp/pmd-bin-$_PMD_VERSION/bin/run.sh ]; then
    wget -nv -O .tmp/pmd-bin-$_PMD_VERSION.zip https://github.com/pmd/pmd/releases/download/pmd_releases%2F$_PMD_VERSION/pmd-bin-$_PMD_VERSION.zip
    unzip .tmp/pmd-bin-$_PMD_VERSION.zip -d .tmp
fi
export PMD_HOME=$(pwd)/.tmp/pmd-bin-$_PMD_VERSION
echo \$PMD_HOME=$PMD_HOME

export PATH=$ANT_HOME/bin:$PATH
