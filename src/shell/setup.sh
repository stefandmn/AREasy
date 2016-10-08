#!/bin/sh

######################################################################################
#
# Default Configuration

optAREasyServerValue="127.0.0.1"
optAREasyPortValue="6506"
optARSystemServerValue="localhost"
optARSystemPortValue="2300"
optARSystemUserNameValue="Demo"
optARSystemPasswordValue=""
SILENTMODE=0

######################################################################################
#
# Functions

# lecho - Logged echo to stdout
#	Arg 0 to N = Data to be echoed
lecho()
{
	[ "${SILENTMODE}" -eq 0 ] && echo "$@"
}


# prompt - Prompt to stdout (echo without
# return at end of line)
#	Arg 0 to N = Data to be echoed
prompt()
{
	ECHOSTYLE=`echo -n`
	if [ "$ECHOSTYLE" = "-n" ]; then
		[ "${SILENTMODE}" -eq 0 ] && echo "$@\c"
	else
		[ "${SILENTMODE}" -eq 0 ] && echo "$@"
	fi
}


# stdin -- Read from stdin
#	return input value
stdin()
{
	read $1
}


# stdinValidate - Read from stdin and validate
# to not be null
#   Arg #1 = Buffer to hold the result
#   Arg #2 = Default value
stdinValidate()
{
	read input

	if [ "$input" = "" ]
		then
			eval $1="$2"
		else
			eval $1="$input"
	fi

	echo
}


# AskContinue - Ask if the specified operation
# is to be installed or continued
#   Arg #1 = Buffer to hold the result
#	ARG #2 - Message to ask
#	ARG #3 - Default answer 'Y' or 'N'
#	return = 0 to NOT install the package
#		   = 1 to install the package
AskContinue()
{
    A=$1
    B=$2
    C=$3

    while [ 1 ]; do
        QUERY_RESULT="-1"
        until test $QUERY_RESULT = Y -o $QUERY_RESULT = y -o $QUERY_RESULT = N -o $QUERY_RESULT = n
        do
            if [ "$C" != "" ]; then
                prompt "$B [$C] "
            else
                prompt "$B "
            fi

            stdin QUERY_RESULT
            if [ "$QUERY_RESULT" = "" ]; then
                QUERY_RESULT="-1"
            fi

            if [ "$QUERY_RESULT" = "-1" -a "$C" != "" ]; then
               QUERY_RESULT="$C"
            fi
            echo
        done
        eval $A="$QUERY_RESULT"

        case $QUERY_RESULT in
            [yY]* ) STATUS=1
                    break
                    ;;
            [nN]*) STATUS=0
                   break
	            ;;
            [qQ] ) lecho "You have requested to cancel the installation."
                   YesOrNo quit "Do you really want to quit installation session ?" "n"
                   case $quit in
                       [yY]* ) UserTermination
                               ;;
                           * ) ANSW="$SV_ANSW"
                   esac
                   ;;
        esac
    done

    return $STATUS
}


# ReadProperty - Read an identifier from a properties file and
# returns his value
#   Arg #1 = Buffer to hold the result
#   Arg #2 = Properties file
#	ARG #3 - Property name
#	return = property value
ReadProperty()
{
    A=$2
    B=$3

	VALUE=`sed '/^\#/d' $A | grep "$B" | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//'`
	eval $1="$VALUE"
}


######################################################################################
#
# Installer Body


# Copying files
INSTHOME=`dirname $0`

if [ -d "$AREASY_HOME" ]
	then
		INSTDIR=$AREASY_HOME
		echo "AREasy installation folder is already defined: $INSTDIR"
	else
		INSTDIR="/opt/snt/areasy"
		prompt "AREasy Destination Folder ($INSTDIR): "
		stdinValidate INSTDIR $INSTDIR
fi

if [ ! -d "$INSTDIR" ]; then
   mkdir -p $INSTDIR
else
	if [ -f "$INSTDIR/cfg/default.properties" ]; then
		# Set default configuration with existing values
		CONFIGFILE=$INSTDIR/cfg/default.properties

		ReadProperty optAREasyServerValue $CONFIGFILE "app.server.host"
		ReadProperty optAREasyPortValue $CONFIGFILE "app.server.port"
		ReadProperty optARSystemServerValue $CONFIGFILE "app.server.default.arsystem.server.name"
		ReadProperty optARSystemPortValue $CONFIGFILE "app.server.default.arsystem.port.number"
		ReadProperty optARSystemUserNameValue $CONFIGFILE "app.server.default.arsystem.user.name"
	fi
fi

echo
echo
echo "Extracting files, please wait..."
tar xf $INSTHOME/%name%-%version%.tar.gz --directory=$INSTDIR
chmod +x $INSTDIR/bin/*.sh
chmod +x $INSTDIR/bin/tools/*.sh
ln -s -f $INSTDIR/bin/areasy.sh $INSTDIR/bin/areasy
AREASY_HOME="$INSTDIR"
PATH=$PATH:$AREASY_HOME/bin
export AREASY_HOME PATH
echo


CONTINUE_INSTALLATION_ANSWER=
while [ "$CONTINUE_INSTALLATION_ANSWER" != "y" ]
do
	echo
	prompt "AREasy Server Name/IP Address ($optAREasyServerValue): "
	stdinValidate optAREasyServerValue $optAREasyServerValue

	prompt "AREasy TCP Port ($optAREasyPortValue): "
	stdinValidate optAREasyPortValue $optAREasyPortValue

	prompt "AR System Server Name/IP Address ($optARSystemServerValue): "
	stdinValidate optARSystemServerValue $optARSystemServerValue

	prompt "AR System TCP Port ($optARSystemPortValue): "
	stdinValidate optARSystemPortValue $optARSystemPortValue

	prompt "AR System User Name ($optARSystemUserNameValue): "
	stdinValidate optARSystemUserNameValue $optARSystemUserNameValue

	prompt "AR System User Password: "
	stdinValidate optARSystemPasswordValue $optARSystemPasswordValue

	echo "Review input data:"
	echo "     AREasy Server:         $optAREasyServerValue:$optAREasyPortValue"
	echo "     AR System Server:      $optARSystemUserNameValue@$optARSystemServerValue:$optARSystemPortValue"

	AskContinue CONTINUE_INSTALLATION_ANSWER "Are you agree with the data input ? (y/n)" "y"
done

echo "------------------------------------------------"
echo
echo
echo "Configuring AREasy runtime instance.."
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.default.arsystem.server.name" -value "$optARSystemServerValue" > /dev/null 2>&1
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.default.arsystem.port.number" -value "$optARSystemPortValue" > /dev/null 2>&1
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.default.arsystem.user.name" -value "$optARSystemUserNameValue" > /dev/null 2>&1
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.default.arsystem.user.password" -value "$optARSystemPasswordValue" > /dev/null 2>&1
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.host" -value "$optAREasyServerValue" > /dev/null 2>&1
$JAVA_HOME/bin/java -jar "$INSTDIR/bin/boot.jar" -config "$INSTDIR/cfg/default.properties" -runtime -action config -property "app.server.port" -value "$optAREasyPortValue" > /dev/null 2>&1
echo
echo


# Write environment variables in user profile
echo "Update user profile.."
PROFILE=""
if [ -f "$HOME/.bash_profile" ]; then
	PROFILE="$HOME/.bash_profile"
else
	if [ -f "$HOME/.profile" ]; then
		PROFILE="$HOME/.profile"
	fi
fi
if [ -f "$PROFILE" ]; then
	ISARHOME=`more $PROFILE | grep "^AREASY_HOME." | wc -l`

	if [ "${ISARHOME}" -eq 0 ]; then
    	echo "\n\nAREASY_HOME=$AREASY_HOME" >> $PROFILE
    	echo "PATH=$PATH:\$AREASY_HOME/bin" >> $PROFILE
    	echo "export PATH AREASY_HOME" >> $PROFILE
    else
		sed -r -e 's/^AREASY_HOME.*$/'AREASY_HOME=INSTDIR'/g' -i $PROFILE
		sed 's|INSTDIR|'"$INSTDIR"'|g' -i $PROFILE
	fi
fi
echo
echo


# Cleaning and close installation session
echo "Cleaning temporary files.."
#rm -rf $TMP/areasy
#sleep 2
echo
echo


echo "Installation complete."
echo
echo
######################################################################################
