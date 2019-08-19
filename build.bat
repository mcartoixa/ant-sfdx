@ECHO OFF
::--------------------------------------------------------------------
:: Usage: "build [clean | compile | test | analysis | package | build | rebuild | release] [/log] [/NoPause] [/?]"
::
::                 /NoPause  - Does not pause after completion
::                 /log      - Creates an extensive log file
::                 /?        - Gets the usage for this script
::--------------------------------------------------------------------



:: Reset ERRORLEVEL
VERIFY OTHER 2>nul
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
IF ERRORLEVEL 1 GOTO ERROR_EXT

SET IVY_VERSION=2.5.0-rc1

SET NO_PAUSE=0
SET PROJECT=build.xml
SET TARGET=build
SET VERBOSITY=info
GOTO ARGS

:SHOW_USAGE
ECHO.
ECHO Usage: "build [clean | compile | test | analysis | package | build | rebuild | release ] [/log] [/NoPause] [/?]"
ECHO.
ECHO.                /NoPause  - Does not pause after completion
ECHO.                /log      - Creates an extensive log file
ECHO.                /?        - Gets the usage for this script
IF "%_ERROR%"=="1" GOTO END_ERROR
GOTO END



:: -------------------------------------------------------------------
:: Builds the project
:: -------------------------------------------------------------------
:BUILD
:: Ivy
IF NOT EXIST ivy MKDIR ivy
PUSHD ivy
IF NOT EXIST ivy.jar (
    powershell.exe -NoLogo -NonInteractive -ExecutionPolicy ByPass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest https://repo1.maven.org/maven2/org/apache/ivy/ivy/$Env:IVY_VERSION/ivy-$Env:IVY_VERSION.jar -OutFile ivy.jar; }"
    IF ERRORLEVEL 1 GOTO END_ERROR
)
POPD
"%JAVA_HOME%\bin\java.exe" -jar ivy\ivy.jar -retrieve "ivy\lib\[conf]\[artifact].[ext]"
IF ERRORLEVEL 1 GOTO END_ERROR

ECHO.
CALL "%ANT_HOME%\bin\ant.bat" -noclasspath -nouserlib -noinput -lib "ivy\lib\test" -Dverbosity=%VERBOSITY% -f %PROJECT% %TARGET%
IF ERRORLEVEL 1 GOTO END_ERROR
GOTO END



:: -------------------------------------------------------------------
:: Parse command line argument values
:: Note: Currently, last one on the command line wins (ex: rebuild clean == clean)
:: -------------------------------------------------------------------
:ARGS
IF "%PROCESSOR_ARCHITECTURE%"=="x86" (
    "C:\Windows\Sysnative\cmd.exe" /C "%0 %*"

    IF ERRORLEVEL 1 EXIT /B 1
    EXIT /B 0
)
::IF NOT "x%~5"=="x" GOTO ERROR_USAGE

:ARGS_PARSE
IF /I "%~1"=="clean"                SET TARGET=clean& RMDIR /S /Q .tmp 2>nul& RMDIR /S /Q tmp 2>nul& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="analysis"             SET TARGET=analysis& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="compile"              SET TARGET=compile& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="test"                 SET TARGET=test& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="package"              SET TARGET=package& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="rebuild"              SET TARGET=rebuild& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="build"                SET TARGET=build& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="release"              SET TARGET=release& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="/log"                 SET VERBOSITY=verbose& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="/NoPause"             SET NO_PAUSE=1& SHIFT & GOTO ARGS_PARSE
IF /I "%~1"=="/?"   GOTO SHOW_USAGE
IF    "%~1" EQU ""  GOTO ARGS_DONE
ECHO [31mUnknown command-line switch[0m %~1 1>&2
GOTO ERROR_USAGE

:ARGS_DONE



:: -------------------------------------------------------------------
:: Set environment variables
:: -------------------------------------------------------------------
:SETENV
CALL build\SetEnv.bat
IF ERRORLEVEL 1 GOTO END_ERROR
ECHO.
GOTO BUILD



:: -------------------------------------------------------------------
:: Errors
:: -------------------------------------------------------------------
:ERROR_EXT
ECHO [31mCould not activate command extensions[0m 1>&2
GOTO END_ERROR

:ERROR_USAGE
SET _ERROR=1
GOTO SHOW_USAGE



:: -------------------------------------------------------------------
:: End
:: -------------------------------------------------------------------
:END_ERROR
ECHO.
ECHO [41m                                                                                [0m
ECHO [41;1mThe build failed                                                                [0m
ECHO [41m                                                                                [0m

:END
@IF NOT "%NO_PAUSE%"=="1" PAUSE
ENDLOCAL
