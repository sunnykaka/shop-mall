#!/bin/bash
clear
for i in $(<project.txt)
do
if [ -d "$i" ]; then
   echo "$i exists" 
else
    git clone git@192.168.1.100:shop-mall/$i.git
fi
done
