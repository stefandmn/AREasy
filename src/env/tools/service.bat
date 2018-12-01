@echo off
if "%OS%" == "Windows_NT" setlocal

rem ---------------------------------------------------------------------------
rem AREasy Runtime
rem Copyright (c) 2007-2018 AREasy.org. All Rights Reserved.
rem
rem NT Service Install/Uninstall script
rem
rem Options
rem 		install                Install the service using AREasy as service name.
rem 		remove                 Remove the service from the System.
rem 		update                 Remove the service from the System.
rem
rem ---------------------------------------------------------------------------

rem Set JVM resources
set "JVMMS=256M"
set "JVMMM=1024M"

rem Detect platform type
set SRVNAME=service.exe

rem Check java version
"%JAVA_HOME%\bin\java.exe" -d64 -version >nul 2>&1

If %ERRORLEVEL% == 1 (
	set SRVNAME=service64.exe
) else (
	set SRVNAME=service64.exe
)

rem Get HOME and BASE folders
set _APP_CUR=%~dp0
for %%? in ("%_APP_CUR%..") do set _APP_BIN=%%~f?
for %%? in ("%_APP_CUR%..\..") do set _APP_HOM=%%~f?

rem Process service action
if exist "%_APP_BIN%\%SRVNAME%" goto okHome
echo The %SRVNAME% was not found...
goto end

rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto okHome
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
goto end 
:okHome

rem Define the service path
set "EXECUTABLE=%_APP_BIN%\%SRVNAME%"

rem Set default Service name
set SERVICE_NAME=AREasy
set PR_DISPLAYNAME=AREasy Runtime
set PR_DESCRIPTION=AREasy Runtime Server for BMC Remedy

if "%1" == "" goto displayUsage
:setServiceName
if %1 == install goto doInstall
if %1 == remove goto doRemove
if %1 == uninstall goto doRemove
if %1 == update goto doUpdate
echo Unknown parameter "%1"
:displayUsage
echo.
echo Usage: service.bat install/remove/update
goto end

:doRemove
rem Remove the service
"%EXECUTABLE%" //DS//%SERVICE_NAME%
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
rem Install the service
echo
echo Installing the service '%SERVICE_NAME%' ...
echo Using AREASY_HOME:     "%_APP_HOM%"
echo Using JAVA_HOME:       "%JAVA_HOME%"
rem Each command line option is prefixed with PR_
set "PR_INSTALL=%EXECUTABLE%"
set "PR_LOGPATH=%_APP_HOM%\logs"
set "PR_CLASSPATH=%_APP_BIN%\boot.jar"
rem Set the server jvm from JAVA_HOME
set "PR_JVM=%JAVA_HOME%\jre\bin\server\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
rem Set the client jvm from JAVA_HOME
set "PR_JVM=%JAVA_HOME%\jre\bin\client\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
set PR_JVM=auto
:foundJvm
echo Using JVM:             "%PR_JVM%"
"%EXECUTABLE%" //IS//%SERVICE_NAME% --StartClass org.areasy.boot.Main --StopClass org.areasy.boot.Main --StartParams -config;"%_APP_HOM%\cfg\default.properties";-mode;server --StopParams -config;"%_APP_HOM%\cfg\default.properties";-mode;client;-action;stop
if not errorlevel 1 goto doUpdate
echo Failed installing '%SERVICE_NAME%' service
goto end

:doUpdate
rem Clear the environment variables. They are not needed any more.
set PR_DISPLAYNAME=
set PR_DESCRIPTION=
set PR_INSTALL=
set PR_LOGPATH=
set PR_CLASSPATH=
set PR_JVM=
rem Set extra parameters
"%EXECUTABLE%" //US//%SERVICE_NAME% --JvmOptions "-Dareasy.home=%_APP_HOM%" --StartMode jvm --StopMode jvm
rem More extra parameters
set "PR_LOGPATH=%_APP_HOM%\logs"
set PR_STDOUTPUT=auto
set PR_STDERROR=auto
set PR_LOGPREFIX=service
"%EXECUTABLE%" //US//%SERVICE_NAME% ++JvmOptions "-Djava.io.tmpdir=%_APP_HOM%\work" --JvmMs %JVMMS% --JvmMx %JVMMM%
echo The service '%SERVICE_NAME%' has been installed.

:end
