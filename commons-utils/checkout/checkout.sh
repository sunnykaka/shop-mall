#!/bin/bash
clear
for i in $(<project.txt)
do
if [ -d "$i" ]; then
    svn update $i
else
    mkdir $i
    svn checkout svn://172.16.0.88/repos/$i/trunk/$i $i
fi
done