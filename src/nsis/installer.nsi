;NS Installer script for AREasy Runtime and API
;$Id: installer.nsi,v 1.1 2009/04/10 07:09:55 Administrator Exp $

;--------------------------------
;Include Modern UI
!include "MUI.nsh"
!include "winmessages.nsh"
!include "nsDialogs.nsh"
!include "FileFunc.nsh"

;--------------------------------
;Configuration
Name "%fullname%"
OutFile "%name%-%version%.exe"
XPStyle on

; Show install details
;ShowInstDetails show


;--------------------------------
;Variables
Var /GLOBAL JavaHome
Var PathEnv ;variable used during install section to store PATH variable content
Var arOutput ;variable used to identify the index of PATH value where AREASY_HOME value is located
Var optDialog
Var optARSystemServer
Var optARSystemPort
Var optAREasyServer
Var optAREasyPort
Var optARSystemUserName
Var optARSystemPassword
Var optService
Var optARSystemServerValue
Var optARSystemPortValue
Var optAREasyServerValue
Var optAREasyPortValue
Var optARSystemUserNameValue
Var optARSystemPasswordValue
Var optServiceValue
Var HaystackOffset ;StrContains - current offset on haystack to copy NeedleLength characters into HayStackBuffer
Var HaystackLength ;StrContains - size of the big string
Var MaxHaystackOffset ;StrContains - maximum offset we can reach
Var NeedleLength ;StrContains - length of string we're looking for
Var HaystackBuffer ;StrContains - substring we get on each iteration
;--------------------------------
;Get install folder from registry if available
InstallDirRegKey HKLM "Software\%fullname%" ""


;--------------------------------
;Interface Settings
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "%srcdir%\nsis\header.bmp"
!define MUI_ABORTWARNING
!define MUI_ICON "%srcdir%\nsis\installer.ico"
!define MUI_UNICON "%srcdir%\nsis\installer.ico"
!define MUI_WELCOMEFINISHPAGE_BITMAP "%srcdir%\nsis\welcome.bmp"

;--------------------------------
; Installer Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
Page custom nsDialogsOptions nsDialogsOptionsLeave
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH


;--------------------------------
;Macros
!insertmacro MUI_LANGUAGE "English"


;--------------------------------
; Custom Macros
!macro StrContains Haystack Needle Output
	; StrContains - Returns starting index of where a string is contained inside another string (case sensitive)
	; Usage: !insertmacro StrContains ${HayStack} ${Needle} ${Output}
	; Parameters:
	; 	HayStack - The string that could contain the substring you're looking for
	; 	Needle   - The string you are looking for
	; 	Output   - -1 if the needle is not in the haystack, or the offset where it exists.

	;Get the lengths of the strings.
	StrLen $HaystackLength ${Haystack}
	StrLen $NeedleLength ${Needle}
	StrCpy $HaystackOffset 0

	;Determine what's the maximum offset we can use to search for the substring
	IntOp $MaxHaystackOffset $HaystackLength - $NeedleLength
	IntOp $MaxHaystackOffset $MaxHaystackOffset - 1

	;Make sure needle is not bigger than haystack
	IntCmp $NeedleLength $HaystackLength LoopStart LoopStart DidNotFindIt

	DidNotFindIt:
		StrCpy $HaystackOffset -1
		Goto Finish

	;Start of substring comparison loop
	LoopStart:
		;copy the substring to a buffer
		StrCpy $HaystackBuffer ${Haystack} $NeedleLength $HaystackOffset

		;If we're done, we return the current haystackoffset
		StrCmpS $HaystackBuffer ${Needle} Finish FigureOutNextStep

	;Did we reach the end, or can we move a bit more
	FigureOutNextStep:
		;Move offset 1 character to the right and see if we can keep going
		IntOp $HaystackOffset $HaystackOffset + 1
		IntCmp $HaystackOffset $MaxHaystackOffset LoopStart LoopStart DidNotFindIt

	Finish:
		StrCpy ${Output} $HaystackOffset
!macroend


;--------------------------------
;Installer Sections

Section Install
SectionIn 1
    ;Check JavaHome env variable
    DetailPrint "Check JavaHome environment variable"
    Call findJava

	;Establish ouput directory.
	SetOutPath "$INSTDIR"

    ;Copy files and folders
	DetailPrint "Copy files and folders"
	File /r "%distdir%\bin"
	File /r "%distdir%\cfg"
	File /r "%distdir%\doc"
	File /r "%distdir%\libs"

	;Store installation registry
	WriteRegStr HKLM "Software\%fullname%" "name" "%fullname%"
	WriteRegStr HKLM "Software\%fullname%" "home" $INSTDIR
	WriteRegStr HKLM "Software\%fullname%" "version" "%version%"

	;Create uninstaller
	WriteUninstaller "$INSTDIR\bin\uninstall.exe"
    ;Write the uninstall keys for Windows
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "DisplayName" "%fullname%"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "InstallLocation" "$INSTDIR"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "UninstallString" "$INSTDIR\bin\uninstall.exe"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "DisplayVersion" "%version%"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "Publisher" "%vendor%"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "URLInfoAbout" "http://areasy.snt.ro"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "DisplayIcon" "$INSTDIR\bin\areasy.exe"

    ;Get estimated size
 	${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
	IntFmt $0 "0x%08X" $0
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%" "EstimatedSize" "$0"

	;Set environment variables
	DetailPrint "Set environment variables"
	;Set AREASY_HOME variable
	WriteRegExpandStr HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "AREASY_HOME" "$INSTDIR"
	;Set PATH variable
	ReadRegStr $PathEnv HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "PATH"

	!insertmacro StrContains $PathEnv "%AREASY_HOME%" $arOutput
	IntCmp $arOutput 0 cmpNull cmpLessThan0 cmpMoreThan0
	cmpNull:
	cmpLessThan0:
		WriteRegExpandStr HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "PATH" "$PathEnv;%AREASY_HOME%\bin"
		SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
	cmpMoreThan0:

	;Set AREasy configuration
	DetailPrint "Set runtime configuration"
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.default.arsystem.server.name" -value "$optARSystemServerValue"'
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.default.arsystem.port.number" -value "$optARSystemPortValue"'
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.default.arsystem.user.name" -value "$optARSystemUserNameValue"'
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.default.arsystem.user.password" -value "$optARSystemPasswordValue"'
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.host" -value "$optAREasyServerValue"'
	ExecWait '$JavaHome\bin\javaw.exe -jar "$INSTDIR\bin\boot.jar" -config "$INSTDIR\cfg\default.properties" -runtime -action config -property "app.server.port" -value "$optAREasyPortValue"'

	;Set OS Service
	StrCmp $optServiceValue "1" 0 +4
		DetailPrint "Register Windows service"
		ExecWait "$INSTDIR\bin\tools\service.bat install"
		SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
SectionEnd


Section Uninstall

	;Delete Windows service.
	DetailPrint "Delete Windows service."
	ExecWait "$INSTDIR\bin\tools\service.bat remove"

    ;Make sure windows knows about the change
	SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000

	;Delete files and folders
	DetailPrint "Delete files and folders"
	Delete "$INSTDIR\bin\uninstall.exe"
	RMDir /r "$INSTDIR\..\work"
	RMDir /r "$INSTDIR\..\logs"
	RMDir /r "$INSTDIR\..\libs"
	RMDir /r "$INSTDIR\..\doc"
	RMDir /r "$INSTDIR\..\cfg"
	RMDir /r "$INSTDIR\..\bin"
	RMDir "$INSTDIR\..\"

	;Delete registry records.
	DetailPrint "Delete registry records."
	DeleteRegKey HKLM "Software\%fullname%"
	;DeleteRegKey /ifempty HKLM "Software\%fullname%"
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\%fullname%"
	DeleteRegValue HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "AREASY_HOME"

    ;Make sure windows knows about the change
	SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
SectionEnd


;--------------------------------
; Custom Functions

Function .onInit
	ReadRegStr $INSTDIR HKLM "Software\%fullname%" "home"

	StrCmp $INSTDIR "" 0 NoError
		;Default installation folder
		StrCpy $INSTDIR "$PROGRAMFILES\%fullname%"
	NoError:
FunctionEnd

Function findJava
    ClearErrors
    ReadEnvStr $JavaHome "JAVA_HOME"

    StrCmp $JavaHome "" 0 NoAbort
    	ReadRegStr $JavaHome HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "JAVA_HOME"

    	StrCmp $JavaHome "" 0 NoAbort
        	MessageBox MB_OK "JavaHome environment variable is not configured. It is recommended to set JavaHome variable using JDK path!"
        	Abort ; causes installer to quit.
	NoAbort:
FunctionEnd


Function nsDialogsOptions
	!insertmacro MUI_HEADER_TEXT "Configuration Options" "Set-up AREasy Runtime server instance."

	nsDialogs::Create 1018
	Pop $optDialog

	${NSD_CreateLabel} 0 5 100 21 "AREasy Server: "
	${NSD_CreateLabel} 270 5 50 21 "TCP Port: "
	${NSD_CreateText} 100 0 150 21 "127.0.0.1"
	Pop $optAREasyServer
	${NSD_CreateText} 320 0 50 21 "6506"
	Pop $optAREasyPort

	${NSD_CreateLabel} 0 30 100 21 "AR System Server: "
	${NSD_CreateLabel} 270 30 50 21 "TCP Port: "
	${NSD_CreateText} 100 25 150 21 "localhost"
	Pop $optARSystemServer
	${NSD_CreateText} 320 25 50 21 "2300"
	Pop $optARSystemPort

	${NSD_CreateLabel} 0 60 100 21 "AR System User: "
	${NSD_CreateText} 100 55 150 21 "Demo"
	Pop $optARSystemUserName

	${NSD_CreateLabel} 0 85 100 21 "Password: "
	${NSD_CreatePassword} 100 80 150 21 ""
	Pop $optARSystemPassword

	${NSD_CreateCheckBox} 0 115 300 21 "Register AREasy Runtime as Windows service "
	Pop $optService

	nsDialogs::Show
FunctionEnd

Function nsDialogsOptionsLeave
	${NSD_GetText} $optAREasyServer $optAREasyServerValue
	${NSD_GetText} $optAREasyPort $optAREasyPortValue
	${NSD_GetText} $optARSystemServer $optARSystemServerValue
	${NSD_GetText} $optARSystemPort $optARSystemPortValue
	${NSD_GetText} $optARSystemUserName $optARSystemUserNameValue
	${NSD_GetText} $optARSystemPassword $optARSystemPasswordValue
	${NSD_GetState} $optService $optServiceValue
FunctionEnd

