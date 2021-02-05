@ECHO OFF & PUSHD %~DP0 & TITLE
>NUL 2>&1 REG.exe query "HKU\S-1-5-19" || (
    ECHO SET UAC = CreateObject^("Shell.Application"^) > "%TEMP%\Getadmin.vbs"
    ECHO UAC.ShellExecute "%~f0", "%1", "", "runas", 1 >> "%TEMP%\Getadmin.vbs"
    "%TEMP%\Getadmin.vbs"
    DEL /f /q "%TEMP%\Getadmin.vbs" 2>NUL
    Exit /b
)
SET /P ST=输入a添加右键菜单，输入d删除右键菜单：
if /I "%ST%"=="a" goto Add
if /I "%ST%"=="d" goto Remove
:Add
reg add "HKEY_CLASSES_ROOT\*\shell\typora-tools"         /t REG_SZ /v "" /d "用 &typora-tools 执行"   /f
reg add "HKEY_CLASSES_ROOT\*\shell\typora-tools"         /t REG_EXPAND_SZ /v "Icon" /d "%~dp0typora-tools.ico" /f
reg add "HKEY_CLASSES_ROOT\*\shell\typora-tools\command" /t REG_SZ /v "" /d "%~dp0typora-tools.bat \"%%1\"" /f
 
exit
:Remove
reg delete "HKEY_CLASSES_ROOT\*\shell\typora-tools" /f
exit