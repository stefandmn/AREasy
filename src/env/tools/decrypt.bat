@echo off

if "%OS%" == "Windows_NT" @setlocal
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal
rem ---------------------------------------------------------------------------
rem AREasy Runtime
rem Copyright (c) 2007-2014 S&T System Integration & Technology Distribution. All Rights Reserved
rem
rem Dedicated Script to return credential format to be used in configuration files.
rem ---------------------------------------------------------------------------


@echo.
@echo Decryption Utility
@echo =======================


if "%JAVA_HOME%" == "" goto error


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


echo Credential decoding..
"%JAVA_HOME%\bin\java" -classpath "%_APP_HOM%\libs\snt-common-1.1.jar" org.areasy.common.data.type.credential.Credential -d %PASSWORD%
goto end


:error
echo Invalid parameter JAVA_HOME


:end
pause
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
