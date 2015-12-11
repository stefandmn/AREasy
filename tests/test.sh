#!/bin/sh

# Get path variables
APP_HOME=`dirname $0`
APP_HOME=$(cd $APP_HOME/../;pwd)


# Check JVM home
if [ "$JAVA_HOME" = "" ]
	then
		echo "Invalid parameter JAVA_HOME"
		echo
		exit 1
fi

# Check ANT home
if [ "$ANT_HOME" = "" ]
	then
		echo "Invalid parameter ANT_HOME"
		echo
		exit 1
fi

# Check AREASY home
if [ "$AREASY_HOME" = "" ]
	then
		echo "Invalid parameter AREASY_HOME"
		echo
		exit 1
fi

# Check input parameters
if [ "$1" = "" ] || [ "$2" = "" ]
	then
		echo "Please specify the test name and index"
		echo
		exit 1
fi

$ANT_HOME/bin/ant -Darg0=$1 -Darg1=$2 -buildfile "$APP_HOME/build.xml" "4.Tests.1.Exec"