@echo off
@color 0C
echo *****************************
echo 批量更新
echo 今天是 %date%
echo 时间是 %time%
echo.

@for /f "delims=" %%i in (project.txt) do svn up %%i
pause