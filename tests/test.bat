@echo off

if "%OS%" == "Windows_NT" setlocal
if "%OS%"=="Windows_NT" setlocal
if "%OS%"=="WINNT" setlocal
rem ---------------------------------------------------------------------------
rem AREasy Testing Environment
rem Copyright (c) 2007-2013 S&T System Integration & Technology Distribution. All Rights Reserved
rem
rem Dedicated Script to run AREasy tests
rem Usage:
rem   test.sh <test name> <test index>
rem ---------------------------------------------------------------------------


rem Get path variables
set APP_CUR=%~dp0
for %%? in ("%_APP_CUR%..") do set APP_HOME=%%~f?

rem Check JVM home
if "%JAVA_HOME%" == ""
(
	echo The "Invalid parameter JAVA_HOME"
	goto end
)

rem Check ANT home
if "%ANT_HOME%" == ""
(
	echo The "Invalid parameter ANT_HOME"
	goto end
)

rem Check AREASY home
if "%AREASY_HOME%" == ""
(
	echo The "Invalid parameter AREASY_HOME"
	goto end
)

rem Check input parameters
if not "%1" == ""
(
	if "%2" == ""
	(
		echo The "Please specify the test name and index"
		goto end
	)

	 goto askRun
)
else
(
	echo The "Please specify the test name and index"
	goto end
)


:askRun
"%ANT_HOME%\bin\ant"  -Darg0=$1 -Darg1=$2 -buildfile="$APP_HOME/build.xml" "4.Tests.1.Exec"
goto end


:end
rem End program