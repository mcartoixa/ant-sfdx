@ECHO OFF
:: Simulates the Salesforce CLI by outputting a json file corresponding to the supplied command parameter.
:: For instance sfdx.cmd force:org:list  will output the content of force\org\list.json

:: Reset ERRORLEVEL
VERIFY OTHER 2>nul
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
IF ERRORLEVEL 1 GOTO ERROR_EXT

SET _COMMAND_PARAMETER=%1%
SET _RESULT_FILE_PATH=%~dp0%_COMMAND_PARAMETER::=\%.json

IF EXIST %_RESULT_FILE_PATH% type %_RESULT_FILE_PATH%
GOTO END



:ERROR_EXT
ECHO [31mCould not activate command extensions[0m 1>&2
GOTO END

:END
ENDLOCAL
