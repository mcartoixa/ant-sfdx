#!/bin/bash

if [ -f ~/.bashrc ]; then
    . ~/.bashrc;
fi
if [ -f ./.env ]; then
    # xargs does not support the -d option on BSD (MacOS X)
    export $(grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 -r)
    grep -a -v -e '^#' -e '^[[:space:]]*$' .env | tr '\n' '\0' | xargs -0 -r printf "\$%s\n"
    echo
fi

if [ -z ${ANT_HOME+x} ]; then
    export $ANT_HOME=/usr/share/ant
fi

echo \$JAVA_HOME=$JAVA_HOME
echo \$ANT_HOME=$ANT_HOME
echo \$PMD_HOME=$PMD_HOME
echo

export PATH=$ANT_HOME/bin:$PATH
