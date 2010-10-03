# Groovy CiteULike code

This repository contains code to process the CiTO data from CiteULike.

## Dependencies ##

All jars on which this code depends are included.

## Running Groovy from the command line

On POSIX systems:

export CLASSPATH=$( echo *.jar . | sed 's/ /:/g')
groovy cul2html.groovy
