@echo off

if "%OS%" == "Windows_NT" setlocal
if "%OS%"=="Windows_NT" setlocal
if "%OS%"=="WINNT" setlocal


rem ---------------------------------------------------------------------------
rem AREasy Runtime
rem Copyright (c) 2007-2019 AREasy.org. All Rights Reserved.
rem
rem Dedicated Script to run AREasy Runtime
rem
rem Usage:
rem    -server ...		run in server mode
rem    -dserver ...		run in server mode and debug
rem    -dclient ...		run in client mode and debug
rem    -runtime ...		run in runtime mode
rem    -druntime ...	run in runtime mode and debug
rem ---------------------------------------------------------------------------


rem Set JVM and Application resources
set "RUNTIME_MIN_MM=256M"
set "RUNTIME_MAX_MM=1024M"
set "MODE=client"
set "EXEC=normal"
set "COMMAND=-Xms%RUNTIME_MIN_MM% -Xmx%RUNTIME_MAX_MM%"

if "%1" == "" goto okHome
if %1 == -dserver goto doServerDebug
if %1 == -druntime goto doRuntimeDebug
if %1 == -client goto okHome
if %1 == -dclient goto doClientDebug
if %1 == -server goto doServer
if %1 == -runtime goto doRuntime
goto okHome


:doServer
set "MODE=server"
set "EXEC=normal"
goto okHome
:doServerDebug
set "MODE=server"
set "EXEC=debug"
goto okHome
:doRuntime
set "MODE=runtime"
set "EXEC=normal"
goto okHome
:doRuntimeDebug
set "MODE=runtime"
set "EXEC=debug"
goto okHome
:doClientDebug
set "MODE=client"
set "EXEC=debug"
goto okHome


:okHome
rem Get AREasy path variables
set _APP_CUR=%~dp0
for %%? in ("%_APP_CUR%..") do set _APP_HOM=%%~f?
if not "%AREASY_HOME%" == "" set "_APP_HOM=%AREASY_HOME%"


rem Check Java runtime prerequisite
if not "%JAVA_HOME%" == "" (
	set "JAVA_PATH=%JAVA_HOME%"
	goto askDebug
)

set KEY_NAME="HKEY_LOCAL_MACHINE\Software\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
for /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) do (
	set ValueName=%%A
	set ValueType=%%B
	set ValueValue=%%C
)
set KEY_NAME="%KEY_NAME:~1,-1%\%ValueValue%"
set VALUE_NAME=JavaHome
if not "%ValueName%" == "" (
	for /F "usebackq skip=2 tokens=1,2*" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) do (
		set ValueName2=%%A
		set ValueType2=%%B
		set JAVA_PATH=%%C
	)
)

if not "%JAVA_PATH%" == "" goto askDebug
set KEY_NAME="HKEY_LOCAL_MACHINE\Software\Wow6432Node\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
for /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) do (
	set ValueName=%%A
	set ValueType=%%B
	set ValueValue=%%C
)
SET KEY_NAME="%KEY_NAME:~1,-1%\%ValueValue%"
SET VALUE_NAME=JavaHome
if not "%ValueName%" == "" (
	for /F "usebackq skip=2 tokens=1,2*" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) do (
		set ValueName2=%%A
		set ValueType2=%%B
		set JAVA_PATH=%%C
	)
)

if not "%JAVA_PATH%" == "" goto askDebug
echo Java is not detected on your environment
goto end


:askDebug
rem Check is debugging is active
if "%EXEC%" == "debug" goto setDebug
goto setExecCommand


:setDebug
rem Set debuging options
set CMDVERBOSE= "-verbose"
if not "%JPDA_TRANSPORT%" == "" goto gotJpdaTransport
set JPDA_TRANSPORT=dt_socket
:gotJpdaTransport
if not "%JPDA_ADDRESS%" == "" goto gotJpdaAddress
set JPDA_ADDRESS=9999
:gotJpdaAddress
if not "%JPDA_SUSPEND%" == ""  goto setDebugCommand
set JPDA_SUSPEND=y
:setDebugCommand
set COMMAND=%COMMAND% -Xdebug -Xnoagent -Xrunjdwp:transport=%JPDA_TRANSPORT%,server=y,suspend=%JPDA_SUSPEND%,address=%JPDA_ADDRESS%


:setExecCommand
rem Set general execution command
set COMMAND=%COMMAND% -jar "%_APP_HOM%\bin\boot.jar"%CMDVERBOSE% "-config" "%_APP_HOM%\cfg\default.properties"
if "%MODE%" == "server" goto setExecServerCommand
if "%MODE%" == "client" goto setExecClientCommand
if "%MODE%" == "runtime" goto setExecRuntimeCommand
goto okCommand


:setExecServerCommand
rem Set execution command for server instance
set COMMAND=%COMMAND% -mode server
shift
goto okCommand


:setExecClientCommand
rem Set execution command for client instance
set COMMAND=%COMMAND% -mode client
goto okCommand


:setExecRuntimeCommand
rem Adapt runtime parameters
shift
goto okCommand


:okCommand
rem Gather all input parameters
set CMD_LINE_ARGS=%1
if ""%1""=="""" goto runCommand
shift
:setupArgs
if ""%1""=="""" goto runCommand
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs


:runCommand
"%JAVA_PATH%\bin\java" %COMMAND% %CMD_LINE_ARGS%
goto end


:end
rem End of program
