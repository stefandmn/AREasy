@echo off

if "%OS%" == "Windows_NT" setlocal
if "%OS%"=="Windows_NT" setlocal
if "%OS%"=="WINNT" setlocal
rem ---------------------------------------------------------------------------
rem AREasy Runtime
rem Copyright (c) 2007-2014 S&T System Integration & Technology Distribution. All Rights Reserved
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
rem Get path variables
set _APP_CUR=%~dp0
for %%? in ("%_APP_CUR%..") do set _APP_HOM=%%~f?
if not "%AREASY_HOME%" == "" set "_APP_HOM=%AREASY_HOME%"

rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto askDebug
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
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
set COMMAND=%COMMAND% -jar "%_APP_HOM%\bin\boot-1.1.jar"%CMDVERBOSE% "-config" "%_APP_HOM%\cfg\default.properties"
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
"%JAVA_HOME%\bin\java" %COMMAND% %CMD_LINE_ARGS%
goto end


:end
rem End program