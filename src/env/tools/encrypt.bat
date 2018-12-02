@echo off

if "%OS%" == "Windows_NT" @setlocal
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal


rem ---------------------------------------------------------------------------
rem AREasy Runtime
rem Copyright (c) 2007-2018 AREasy.org. All Rights Reserved.
rem
rem Dedicated Script to return credential format to be used in configuration files.
rem ---------------------------------------------------------------------------


@echo.
@echo Password Encrypt Utility
@echo ========================


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


set _APP_CUR=%~dp0
for %%? in ("%_APP_CUR%..") do set _APP_BIN=%%~f?
for %%? in ("%_APP_CUR%..\..") do set _APP_HOM=%%~f?


@echo.
> %TMP%\~userin.vbs echo WScript.StdOut.WriteLine()
>> %TMP%\~userin.vbs echo WScript.StdOut.Write("Please enter the password: ")
>> %TMP%\~userin.vbs echo ar_pass = WScript.StdIn.ReadLine()
>> %TMP%\~userin.vbs echo WScript.StdOut.WriteLine()
>> %TMP%\~userin.vbs echo WScript.StdOut.WriteLine()

>> %TMP%\~userin.vbs echo Set wshShell = CreateObject("Wscript.Shell")
>> %TMP%\~userin.vbs echo Set fs = CreateObject("Scripting.FileSystemObject")
>> %TMP%\~userin.vbs echo tmpDir = wshShell.ExpandEnvironmentStrings("%TEMP%")
>> %TMP%\~userin.vbs echo strFileName = fs.BuildPath(tmpDir, "~userin.bat")
>> %TMP%\~userin.vbs echo strFileName = fs.GetAbsolutePathName(strFileName)
>> %TMP%\~userin.vbs echo Set ts = fs.OpenTextFile(strFileName, 2, True)
>> %TMP%\~userin.vbs echo ts.WriteLine "set PASSWORD=" ^& ar_pass
>> %TMP%\~userin.vbs echo ts.Close
goto userin


:userin
regsvr32 /s scriptpw.dll
cscript //nologo %TMP%\~userin.vbs
regsvr32 /s /u scriptpw.dll
del %TMP%\~userin.vbs
call %TMP%\~userin.bat


echo Credential encoding..
"%JAVA_PATH%\bin\java" -classpath "%_APP_HOM%\libs\areasy.jar" org.areasy.common.data.type.credential.Credential -e %PASSWORD%
goto end


:end
pause
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
