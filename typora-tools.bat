@echo off
if exist %1\ (echo dir not permitted) else (java -Dfile.encoding=utf-8 -jar C:\Users\Heygo\Desktop\Codes\typora-tools\typora-tools.jar %1)
pause