#!/bin/sh

# ---------------------------------------------------------------------------
# AREasy Runtime
# Copyright (c) 2007-2018 AREasy.org. All Rights Reserved.
#
# Dedicated Script to return credential format to be used in configuration files.
# ---------------------------------------------------------------------------


# Caller home
_APP_HOME=`dirname $0`
_APP_HOME=$(cd $_APP_HOME/../../;pwd)

# Check JVM home
if [[ "$JAVA_HOME" = "" ]]
	then
		echo "Invalid parameter JAVA_HOME"
		echo
		exit
fi

echo "Please enter the password: "
read PASSWORD

echo Credential decoding..
"$JAVA_HOME/bin/java" -classpath "$_APP_HOME/libs/areasy.jar" org.areasy.common.data.type.credential.Credential -d $PASSWORD

