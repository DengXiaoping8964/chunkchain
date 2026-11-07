@echo off
rem Build ChunkChain for Minecraft 1.18.2 (Fabric). JDK17 + Gradle 8.7 reused from D:.
setlocal
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "GRADLE_USER_HOME=D:\Dev\chunkchain-gradle-home"
call "D:\Dev\gradle\gradle-8.7\bin\gradle.bat" -p "%~dp0." build --console=plain --no-daemon
echo.
echo Jar output: %~dp0build\libs
endlocal