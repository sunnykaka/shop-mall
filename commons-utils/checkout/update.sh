#!/bin/bash
clear

for i in $(<project.txt)
do
svn update $i
done