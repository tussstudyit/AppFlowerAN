@echo off
cd /d "%~dp0..\web-admin"
call mvn.cmd spring-boot:run 1>"%~dp0..\runtime-logs\web-admin.out.log" 2>"%~dp0..\runtime-logs\web-admin.err.log"
