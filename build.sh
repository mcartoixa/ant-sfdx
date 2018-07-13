#!/bin/bash

source bin/.bashrc

_PROJECT=build.xml
_TARGET=build
_VERBOSITY=info
_IVY_VERSION=2.5.0-rc1


usage() {
    cat <<EOF
Usage: $0 [TARGET] [OPTION]
    TARGET          analysis|build|clean|compile|package|rebuild|test
    OPTION
        --log       Creates an extensive build.log file
        --help      Shows this help
EOF
}

error() {
    echo
    echo -e "\033[0;31m${1}\033[0m"

    failed
}

failed() {
    echo
    echo -e "\033[41m                                                                                \033[0m"
    echo -e "\033[1;41mThe build failed                                                                \033[0m"
    echo -e "\033[41m                                                                                \033[0m"

    exit 1
}

# Parse command line argument values
# Note: Currently, last one on the command line wins (ex: rebuild clean == clean)
for i in "$@"; do
    case $1 in
        analysis|build|clean|compile|package|rebuild|test) _TARGET=$1 ;;
        -l|--log) _VERBOSITY=verbose ;;
        --help) usage ;;
        *) usage; error "Unknown option" ;;
    esac
    shift
done

# Ivy
if [ ! -d ivy ]; then mkdir ivy; fi
if [ ! -f ivy/ivy.jar ]; then wget -nv  --show-progress -O ivy/ivy.jar https://repo1.maven.org/maven2/org/apache/ivy/ivy/$_IVY_VERSION/ivy-$_IVY_VERSION.jar; fi
if [ $? -ne 0 ]; then
    failed
fi
$JAVA_HOME/bin/java -jar ivy/ivy.jar -retrieve "ivy/lib/[conf]/[artifact].[ext]"
if [ $? -ne 0 ]; then
    failed
fi

echo
$ANT_HOME/bin/ant -noclasspath -nouserlib -noinput -lib "ivy/lib/test" -logger org.apache.tools.ant.listener.AnsiColorLogger -Dverbosity=$_VERBOSITY -f $_PROJECT $_TARGET
if [ $? -ne 0 ]; then
    failed
fi
