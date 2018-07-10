@ECHO OFF

:: Reset ERRORLEVEL
VERIFY OTHER 2>nul


SET _ANT_VERSION=1.9.12
SET _PMD_VERSION=6.5.0


:: -------------------------------------------------------------------
:: Set environment variables
:: -------------------------------------------------------------------
CALL :SetLocalEnvHelper 2>nul

:: Java
CALL :SetJavaHomeHelper > nul 2>&1
IF ERRORLEVEL 1 GOTO ERROR_JDK
ECHO SET JAVA_HOME=%JAVA_HOME%

:: Ant
SET ANT_HOME=%CD%\.tmp\apache-ant-%_ANT_VERSION%
IF NOT EXIST "%ANT_HOME%\bin\ant.bat" (
    IF NOT EXIST .tmp MKDIR .tmp
    powershell.exe -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest http://apache.mirrors.ovh.net/ftp.apache.org/dist//ant/binaries/apache-ant-$Env:_ANT_VERSION-bin.zip -OutFile .tmp\apache-ant-$Env:_ANT_VERSION-bin.zip; }"
    IF ERRORLEVEL 1 GOTO ERROR_ANT
    powershell.exe  -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "Expand-Archive -Path .tmp\apache-ant-$Env:_ANT_VERSION-bin.zip -DestinationPath .tmp -Force"
    IF ERRORLEVEL 1 GOTO ERROR_ANT
)
ECHO SET ANT_HOME=%ANT_HOME%

:: PMD
:: Best would to be able to manage PMD with Apache Ivy but this looks like an impossible task...
SET PMD_HOME=%CD%\.tmp\pmd-bin-%_PMD_VERSION%
IF NOT EXIST "%PMD_HOME%\bin\pmd.bat" (
    IF NOT EXIST .tmp MKDIR .tmp
    powershell.exe -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest https://github.com/pmd/pmd/releases/download/pmd_releases%%2F$Env:_PMD_VERSION/pmd-bin-$Env:_PMD_VERSION.zip -OutFile .tmp\pmd-bin-$Env:_PMD_VERSION.zip; }"
    IF ERRORLEVEL 1 GOTO ERROR_PMD
    powershell.exe  -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "Expand-Archive -Path .tmp\pmd-bin-$Env:_PMD_VERSION.zip -DestinationPath .tmp -Force"
    IF ERRORLEVEL 1 GOTO ERROR_PMD
)
ECHO SET PMD_HOME=%PMD_HOME%

SET PATH=%ANT_HOME%\bin;%PATH%

:: sfdx hates spaces in LOCALAPPDATA
FOR %%d IN ("%LOCALAPPDATA%") DO SET LOCALAPPDATA=%%~sd
GOTO END



:SetLocalEnvHelper
IF EXIST .env (
    FOR /F "eol=# tokens=1* delims==" %%i IN (.env) DO (
        SET "%%i=%%j"
        ECHO SET %%i=%%j
    )
    ECHO.
)
EXIT /B 0



:SetJavaHomeHelper
SET JAVA_HOME=
FOR /F "tokens=1,2*" %%i IN ('REG QUERY "HKLM\SOFTWARE\JavaSoft\Java Development Kit\1.8" /V JavaHome') DO (
    IF "%%i"=="JavaHome" (
        SET "JAVA_HOME=%%k"
    )
)
IF "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
    FOR /F "tokens=1,2*" %%i IN ('REG QUERY "HKLM\SOFTWARE\Wow6432Node\JavaSoft\Java Development Kit\1.8" /V JavaHome') DO (
        IF "%%i"=="JavaHome" (
            SET "JAVA_HOME=%%k"
        )
    )
)
IF "%JAVA_HOME%"=="" EXIT /B 1
EXIT /B 0



:SetSfdxHomeHelper
:: Interpreting a default value from the registry is cumbersome...
SET SFDX_HOME=
SET _SFDX_HOME=
FOR /F "tokens=1* delims=Z" %%i IN ('REG QUERY "HKLM\SOFTWARE\sfdx" /VE') DO (
    IF "%%j" NEQ "" (
        SET "_SFDX_HOME=%%j"
    )
)
IF "%_SFDX_HOME%"=="" (
    FOR /F "tokens=1* delims=Z" %%i IN ('REG QUERY "HKCU\SOFTWARE\sfdx" /VE') DO (
        IF "%%j" NEQ "" (
            SET "_SFDX_HOME=%%j"
        )
    )
)
IF "%_SFDX_HOME%"=="" EXIT /B 1
SET SFDX_HOME=%_SFDX_HOME:~4%
SET SFDX_PATH=%SFDX_HOME%\bin
SET _SFDX_HOME=
EXIT /B 0


:ERROR_EXT
ECHO [31mCould not activate command extensions[0m 1>&2
GOTO END_ERROR

:ERROR_JDK
ECHO [31mCould not find Java JDK 8[0m 1>&2
GOTO END_ERROR

:ERROR_ANT
ECHO [31mCould not install Apache Ant 1.10[0m 1>&2
GOTO END_ERROR

:ERROR_PMD
ECHO [31mCould not install PMD 6.x[0m 1>&2
GOTO END_ERROR

:END_ERROR
EXIT /B 1

:END
