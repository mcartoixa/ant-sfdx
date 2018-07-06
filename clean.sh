#!/bin/bash
set -e

if [ -d .tmp ]; then rm -Rfd .tmp; fi
if [ -d ivy ]; then rm -Rfd ivy; fi
if [ -d tmp ]; then rm -Rfd tmp; fi

if [ -f build.log ]; then rm -f build.log; fi
