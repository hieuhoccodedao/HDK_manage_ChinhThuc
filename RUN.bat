@echo off
chcp 65001 > nul
title HDK Management

cd /d "%~dp0"

echo ==============================================
echo [HDK Management] Dang khoi dong ung dung...
echo ==============================================

"C:\Program Files\Java\jdk-21\bin\java.exe" ^
  -Dfile.encoding=UTF-8 ^
  -Dstdout.encoding=UTF-8 ^
  -Dstderr.encoding=UTF-8 ^
  -classpath "build\classes;lib\mysql-connector-j-8.0.33.jar;lib\flatlaf-3.5.1.jar;lib\emoji-java-5.1.1.jar;lib\itextpdf-5.5.13.3.jar" ^
  hdkmanagement.HDKManagement

echo.
pause
