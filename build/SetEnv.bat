@ECHO OFF

:: Reset ERRORLEVEL
VERIFY OTHER 2>nul



CALL :SetVersionsEnvHelper 2>nul



:: -------------------------------------------------------------------
:: Set environment variables
:: -------------------------------------------------------------------
CALL :SetLocalEnvHelper 2>nul

:: Java
IF NOT "%1" == "/useCurrentJavaHome" (
    CALL :SetJavaHomeHelper > nul 2>&1
    IF ERRORLEVEL 1 GOTO ERROR_JDK
)
ECHO SET JAVA_HOME=%JAVA_HOME%

:: Ant
SET ANT_HOME=%CD%\.tmp\apache-ant-%_ANT_VERSION%
IF NOT EXIST "%ANT_HOME%\bin\ant.bat" (
    IF NOT EXIST .tmp MKDIR .tmp
    powershell.exe -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest http://mirrors.ircam.fr/pub/apache//ant/binaries/apache-ant-$Env:_ANT_VERSION-bin.zip -OutFile .tmp\apache-ant-$Env:_ANT_VERSION-bin.zip; }"
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

IF NOT EXIST "%CD%\.tmp\cloc.exe" (
    IF NOT EXIST .tmp MKDIR .tmp
    powershell.exe -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest https://github.com/AlDanial/cloc/releases/download/$Env:_CLOC_VERSION/cloc-$Env:_CLOC_VERSION.exe -OutFile .tmp\cloc.exe; }"
    IF ERRORLEVEL 1 GOTO ERROR_CLOC
)


SET PATH=%ANT_HOME%\bin;%PATH%
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



:SetVersionsEnvHelper
IF EXIST build\versions.env (
    FOR /F "eol=# tokens=1* delims==" %%i IN (build\versions.env) DO (
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



:ERROR_EXT
ECHO [31mCould not activate command extensions[0m 1>&2
GOTO END_ERROR

:ERROR_JDK
ECHO [31mCould not find Java JDK 8[0m 1>&2
GOTO END_ERROR

:ERROR_ANT
ECHO [31mCould not install Apache Ant %_ANT_VERSION%[0m 1>&2
GOTO END_ERROR

:ERROR_PMD
ECHO [31mCould not install PMD %_PMD_VERSION%[0m 1>&2
GOTO END_ERROR

:ERROR_CLOC
ECHO [31mCould not install CLOC  %_CLOC_VERSION%[0m 1>&2
GOTO END_ERROR

:END_ERROR
EXIT /B 1

:END
