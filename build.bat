@ECHO OFF

REM Define variables
SET APP_NAME=framework_2624
SET APP_DIR=%~dp0
SET SRC_DIR=%APP_DIR%mg
SET LIB_DIR=%APP_DIR%lib
SET BUILD_DIR=%APP_DIR%build
SET DEST_DIR=E:\dev\App-temoin-framework-spring\lib
SET JAR_FILE=%BUILD_DIR%\%APP_NAME%.jar

REM Create necessary directories
IF EXIST "%BUILD_DIR%" (
    ECHO Build directory already exists. Deleting it.
    RD /S /Q "%BUILD_DIR%"
)
MKDIR "%BUILD_DIR%"

REM Find Java files and list them in sources.txt
echo Finding Java files...
dir /s /b %SRC_DIR%\*.java > %BUILD_DIR%\sources.txt

REM Check if sources.txt is empty and delete it if it is
for %%i in (%BUILD_DIR%\sources.txt) do if %%~zi==0 del %BUILD_DIR%\sources.txt

REM Compile Java files if any are found
if exist %BUILD_DIR%\sources.txt (
    echo Compiling Java files...
    javac -parameters -cp %LIB_DIR%\* -d %BUILD_DIR% @%BUILD_DIR%\sources.txt > %BUILD_DIR%\compile.log 2>&1
    if errorlevel 1 (
        echo Compilation failed. See compile.log for details.
        type %BUILD_DIR%\compile.log
        exit /b 1
    )
) else (
    echo No Java files found to compile.
)

REM Copy library JAR files to the build directory
XCOPY /Y "%LIB_DIR%\*.jar" "%BUILD_DIR%"

REM Create a JAR file including the compiled classes and library JARs
jar cvf "%JAR_FILE%" -C "%BUILD_DIR%" .

REM Copy the JAR file to the destination directory
IF NOT EXIST "%DEST_DIR%" (
    MKDIR "%DEST_DIR%"
)
XCOPY /Y "%JAR_FILE%" "%DEST_DIR%\"
