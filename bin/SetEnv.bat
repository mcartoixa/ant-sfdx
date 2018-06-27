@ECHO OFF

:: Reset ERRORLEVEL
VERIFY OTHER 2>nul

:: -------------------------------------------------------------------
:: Set environment variables
:: -------------------------------------------------------------------
CALL :SetLocalEnvHelper 2>nul

CALL :SetJavaHomeHelper > nul 2>&1
IF ERRORLEVEL 1 GOTO ERROR_JDK
ECHO SET JAVA_HOME=%JAVA_HOME%

ECHO SET ANT_HOME=%ANT_HOME%
IF NOT EXIST "%ANT_HOME%\bin\ant.bat" GOTO ERROR_ANT

ECHO SET PMD_HOME=%PMD_HOME%
IF NOT EXIST "%PMD_HOME%\bin\pmd.bat" GOTO ERROR_PMD

SET PATH=%ANT_HOME%\bin;%PATH%



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
ECHO [31mCould not activate command extensions[0m
GOTO END_ERROR

:ERROR_JDK
ECHO [31mCould not find Java JDK 8[0m
GOTO END_ERROR

:ERROR_ANT
ECHO [31mCould not find Apache Ant 1.10[0m
GOTO END_ERROR

:ERROR_PMD
ECHO [31mCould not find PMD 6.x[0m
GOTO END_ERROR

:END_ERROR
EXIT /B 1

:END
