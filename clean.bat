@ECHO OFF

SET NO_PAUSE=0

IF "%PROCESSOR_ARCHITECTURE%"=="x86" (
    "C:\Windows\Sysnative\cmd.exe" /C "%0 %*"

    IF ERRORLEVEL 1 EXIT /B 1
    EXIT /B 0
)
::IF NOT "x%~5"=="x" GOTO ERROR_USAGE

:ARGS_PARSE
IF /I "%~1"=="/NoPause"             SET NO_PAUSE=1& SHIFT & GOTO ARGS_PARSE
IF    "%~1" EQU ""  GOTO ARGS_DONE
ECHO [31mUnknown command-line switch[0m %~1 1>&2
GOTO END

:ARGS_DONE

IF EXIST .tmp RMDIR /S /Q .tmp
IF EXIST ivy RMDIR /S /Q ivy
IF EXIST tmp RMDIR /S /Q tmp

IF EXIST build.log DEL /F build.log

:END
@IF NOT "%NO_PAUSE%"=="1" PAUSE
