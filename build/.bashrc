#!/bin/bash

error() {
    echo
    echo -e "\033[0;31m${1}\033[0m"
}

if [ -f ~/.bashrc ]; then
    . ~/.bashrc;
fi

if [ -f ./build/versions.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' build/versions.env | tr '\n' '\0' | xargs -0 )
    grep -a -v -e '^#' -e '^[[:space:]]*$' build/versions.env | tr '\n' '\0' | xargs -0 printf "\$%s\n"
    echo
fi



if [ ! -d .tmp ]; then mkdir .tmp; fi

case "$-" in
    *i*) _wget_interactive_options="--show-progress" ;;
      *) _wget_interactive_options= ;;
esac



#Ant
export ANT_HOME=$(pwd)/.tmp/apache-ant-$_ANT_VERSION
if [ ! -f $_ANT_HOME/bin/ant ]; then
    wget -nv $_wget_interactive_options -O .tmp/apache-ant-$_ANT_VERSION-bin.tar.gz https://archive.apache.org/dist/ant/binaries/apache-ant-$_ANT_VERSION-bin.tar.gz
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



if [ -f ./.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0)
    grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 printf "\$%s\n"
    echo
fi

if [ ! -d $JAVA_HOME ]; then error "Could not find Java JDK 8"; fi
echo \$JAVA_HOME=$JAVA_HOME


export PATH=$ANT_HOME/bin:$PATH
