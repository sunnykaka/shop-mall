@echo off
@color 0C
echo.
echo 今天是 %date%
echo 时间是 %time%
echo.
@for /f "delims=" %%i in (project.txt) do if exist %%i (
echo 更新%%i
svn update %%i)else (
mkdir %%i
echo 建立文件夹%%i 准备checkout
svn checkout svn://172.16.0.88/repos/%%i/trunk/%%i %%i
)

pause