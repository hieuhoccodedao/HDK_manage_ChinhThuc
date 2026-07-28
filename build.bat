@echo off
chcp 65001 > NUL
mkdir build\classes 2> NUL
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d build\classes -cp "lib/*;src" @sources.txt 2> error.log
type error.log
