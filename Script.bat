@ECHO OFF

REM Define variables
SET APP_NAME=Framework
SET APP_DIR=%~dp0
SET SRC_DIR=%APP_DIR%mg
SET LIB_DIR=%APP_DIR%lib
SET JAR_FILE=D:\dev\helloworld\lib\%APP_NAME%.jar

REM Create a temporary directory and copy compiled classes, web, lib, and web.xml into it
IF EXIST "..\temp%APP_NAME%" (
    ECHO Temporary directory already exists. Deleting it.
    RD /S /Q "..\temp%APP_NAME%"
)
MKDIR "..\temp%APP_NAME%"
MKDIR "..\temp%APP_NAME%\tempJava"
MKDIR "..\temp%APP_NAME%\tempclass"
MKDIR "..\temp%APP_NAME%\templib"

for /R "%SRC_DIR%" %%G IN ("*.java") DO (
    XCOPY /Y "%%G" "..\temp%APP_NAME%\tempJava"
)

REM Compile Java classes
javac -parameters -cp %LIB_DIR%\* -d "..\temp%APP_NAME%\tempclass" "..\temp%APP_NAME%\tempJava\*.java"

REM Copy library JAR files to the temporary directory
XCOPY /Y "%LIB_DIR%\*.jar" "..\temp%APP_NAME%\templib"

REM Create a JAR file including the compiled classes and library JARs
jar cvf "%JAR_FILE%" -C "..\temp%APP_NAME%\tempclass" . 
