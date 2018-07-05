#!/bin/bash

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

if [ -z ${ANT_HOME+x} ]; then
    export $ANT_HOME=/usr/share/ant
fi
echo \$ANT_HOME=$ANT_HOME

# Best would to be able to manage PMD with Apache Ivy but this looks like an impossible task...
if [ ! -f .tmp/pmd-bin-$_PMD_VERSION/bin/run.sh ]; then
    wget -nv -O .tmp/pmd-bin-$_PMD_VERSION.zip https://github.com/pmd/pmd/releases/download/pmd_releases%2F$_PMD_VERSION/pmd-bin-$_PMD_VERSION.zip
    unzip .tmp/pmd-bin-$_PMD_VERSION.zip -d .tmp
fi
export PMD_HOME=$(pwd)/.tmp/pmd-bin-$_PMD_VERSION
echo \$PMD_HOME=$PMD_HOME

export PATH=$ANT_HOME/bin:$PATH
