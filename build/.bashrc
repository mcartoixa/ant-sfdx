#!/bin/bash

if [ -f ~/.bashrc ]; then
    . ~/.bashrc;
fi

if [ -f ./build/versions.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' build/versions.env | tr '\n' '\0' | xargs -0 )
    grep -a -v -e '^#' -e '^[[:space:]]*$' build/versions.env | tr '\n' '\0' | xargs -0 printf "\$%s\n"
    echo
fi

echo \$JAVA_HOME=$JAVA_HOME

if [ -f ./.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0)
    grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 printf "\$%s\n"
    echo
fi

if [ ! -d .tmp ]; then mkdir .tmp; fi

case "$-" in
    *i*) _wget_interactive_options="--show-progress" ;;
      *) _wget_interactive_options= ;;
esac



#Node.js
export NODEJS_HOME=$(pwd)/.tmp/node-v$_NODEJS_VERSION-linux-x64
if [ ! -f $NODEJS_HOME/npm ]; then
    wget -nv $_wget_interactive_options -O .tmp/node-v$_NODEJS_VERSION-linux-x64.tar.gz https://nodejs.org/dist/v$_NODEJS_VERSION/node-v$_NODEJS_VERSION-linux-x64.tar.gz
    tar -xzvf .tmp/node-v$_NODEJS_VERSION-linux-x64.tar.gz -C .tmp
fi
echo \$NODEJS_HOME=$NODEJS_HOME

#SFDX CLI
export SFDX_HOME=$(pwd)/.tmp/node_modules/.bin
if [ ! -f $SFDX_HOME/sfdx ]; then
    cd .tmp
    $NODEJS_HOME/npm install sfdx-cli --cache npm-cache
    cd ..
fi
echo \$SFDX_HOME=$SFDX_HOME

#Ant
export ANT_HOME=$(pwd)/.tmp/apache-ant-$_ANT_VERSION
if [ ! -f $_ANT_HOME/bin/ant ]; then
    wget -nv $_wget_interactive_options -O .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz http://mirrors.ircam.fr/pub/apache//ant/binaries/apache-ant-$_ANT_VERSION-bin.tar.gz
    tar -xzvf .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz -C .tmp
fi
echo \$ANT_HOME=$ANT_HOME

# PMD
# Best would to be able to manage PMD with Apache Ivy but this looks like an impossible task...
export PMD_HOME=$(pwd)/.tmp/pmd-bin-$_PMD_VERSION
if [ ! -f $PMD_HOME/bin/run.sh ]; then
    wget -nv $_wget_interactive_options -O .tmp/pmd-bin-$_PMD_VERSION.zip https://github.com/pmd/pmd/releases/download/pmd_releases%2F$_PMD_VERSION/pmd-bin-$_PMD_VERSION.zip
    unzip .tmp/pmd-bin-$_PMD_VERSION.zip -d .tmp
fi
echo \$PMD_HOME=$PMD_HOME
echo

if [ ! -f $(pwd)/.tmp/cloc.pl ]; then
    wget -nv $_wget_interactive_options -O .tmp/cloc.pl https://github.com/AlDanial/cloc/releases/download/$_CLOC_VERSION/cloc-$_CLOC_VERSION.pl
fi

export PATH=$SFDX_HOME:$ANT_HOME/bin:$PATH
