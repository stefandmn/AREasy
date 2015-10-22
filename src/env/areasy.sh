#!/bin/sh

# ---------------------------------------------------------------------------
# AREasy Runtime
# Copyright (c) 2007-2014 S&T System Integration & Technology Distribution. All Rights Reserved
#
# Dedicated Script to run AREasy Runtime
# Usage:
#    -server   ...		run in server mode
#    -dserver  ...		run in server mode and debug
#	 -dclient  ...		run in client mode and debug
#    -runtime  ...		run in runtime mode
#    -druntime ...		run in runtime mode and debug
# ---------------------------------------------------------------------------

# Set JVM and Application resources
RUNTIME_MIN_MM=64M
RUNTIME_MAX_MM=256M
MODE=client
EXEC=normal


# Get path variables
_APP_HOME=`dirname $0`
_APP_HOME=$(cd $_APP_HOME/../;pwd)


# Check JVM home
if [ "$JAVA_HOME" = "" ]
	then
		echo "Invalid parameter JAVA_HOME"
		echo
		exit
fi


if [ "$1" = "-server" ]
	then 
		MODE=server
		EXEC=normal
fi
if [ "$1" = "-dserver" ]
	then 
		MODE=server
		EXEC=debug
fi
if [ "$1" = "-client" ]
	then 
		MODE=client
		EXEC=normal
fi
if [ "$1" = "-dclient" ]
	then 
		MODE=client
		EXEC=debug
fi
if [ "$1" = "-runtime" ]
	then 
		MODE=runtime
		EXEC=normal
fi
if [ "$1" = "-druntime" ]
	then 
		MODE=runtime
		EXEC=debug
fi


if [ "$MODE" = "server" ]
	then
		RUNTIME_MIN_MM=128M
		RUNTIME_MAX_MM=1024M
fi
if [ "$MODE" = "runtime" ]
	then
		RUNTIME_MIN_MM=128M
		RUNTIME_MAX_MM=2024M
fi

COMMAND=`echo -Xms$RUNTIME_MIN_MM -Xmx$RUNTIME_MAX_MM`


if [ "$EXEC" = "debug" ]
	then
		# Set debugging parameters
		if [ "$JPDA_TRANSPORT" = "" ]
			then
				JPDA_TRANSPORT=dt_socket
		fi
		if [ "$JPDA_ADDRESS" = "" ]
			then
				JPDA_ADDRESS=9999
		fi
		if [ "$JPDA_SUSPEND" = "" ]
			then
				JPDA_SUSPEND=y
		fi

		CMDVERBOSE=-verbose
		COMMAND=`echo $COMMAND -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=${JPDA_TRANSPORT},server=y,suspend=${JPDA_SUSPEND},address=${JPDA_ADDRESS}`
fi

COMMAND=`echo $COMMAND -jar "$_APP_HOME/bin/boot-1.1.jar" $CMDVERBOSE "-config" "$_APP_HOME/cfg/default.properties"`


if [ "$MODE" = "server" ]
	then
		COMMAND=`echo $COMMAND -mode server`
fi
if [ "$MODE" = "client" ]
	then
		COMMAND=`echo $COMMAND -mode client`
fi


$JAVA_HOME/bin/java $COMMAND "$@"
