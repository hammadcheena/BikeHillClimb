@echo off
set V=8.11.1
set D=%USERPROFILE%\.gradle\wrapper\dists\gradle-%V%-bin
if not exist "%D%\gradle-%V%\bin\gradle.bat" (
 mkdir "%D%" 2>nul
 powershell -NoProfile -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%V%-bin.zip' -OutFile '%D%\gradle.zip'"
 powershell -NoProfile -Command "Expand-Archive -Force '%D%\gradle.zip' '%D%'"
)
call "%D%\gradle-%V%\bin\gradle.bat" %*
