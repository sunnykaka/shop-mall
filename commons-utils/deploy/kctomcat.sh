#!/bin/sh
#kill tomcat pid & Clean file
#
pidlist=`ps aux|grep java | grep -v "grep" |awk '{print $2}'`

#ps -u $USER|grep "java"|grep -v "grep"
  
echo -e "tomcat Id list:\n====================\n$pidlist"

if [ "$pidlist" = "" ]
then
  echo "no tomcat pid alive"
else
  for pid in ${pidlist}
 {
   kill -9 $pid
  echo "KILL $pid:"
  echo -e "service stop success \a\n===================="
  }

fi


echo -e "****start RM boss****"

cd /opt/appserver/
 
rm -rf ./boss/conf/Catalina/
  echo -e "*******KO boss Catalina*******"
rm -rf ./boss/logs/*
  echo -e "****KO boss logs****"
rm -rf ./boss/temp/*
  echo -e "****KO boss temp****"
rm -rf ./boss/work/*
  echo -e "****KO boss work****"
rm -rf ./boss/webapps/boss/
  echo -e "****KO boss apps****"

sleep 3

rm -rf ./boss/webapps/boss.war
  echo -e "****KO boss.war*****"

  echo -e "*********start RM buy*********"

rm -rf ./buy/conf/Catalina/
  echo -e "**KO buy Catalina**"
rm -rf ./buy/logs/*
  echo -e "****KO buy logs****"
rm -rf ./buy/temp/*
  echo -e "****KO buy temp****"
rm -rf ./buy/work/*
  echo -e "****KO buy work****"
rm -rf ./buy/webapps/buyerSystem/
  echo -e "****KO buy apps****"

sleep 3

rm -rf ./buy/webapps/buyerSystem.war
  echo -e "****KO buy.war*****"

  echo -e "*********start RM bug*********"

rm -rf ./bug/conf/Catalina/
  echo -e "**KO bug Catalina**"
rm -rf ./bug/logs/*
  echo -e "****KO bug logs****"
rm -rf ./bug/temp/*
  echo -e "****KO bug temp****"
rm -rf ./bug/work/*
  echo -e "****KO bug work****"
rm -rf ./bug/webapps/bugtracking/
  echo -e "****KO bug apps****"

sleep 3

rm -rf ./bug/webapps/bugtracking.war
  echo -e "****KO bug.war*****"
 
echo -e "*********start RM crm*********"

rm -rf ./crm/conf/Catalina/
  echo -e "**KO crm Catalina**"
rm -rf ./crm/logs/*
  echo -e "****KO crm logs****"
rm -rf ./crm/temp/*
  echo -e "****KO crm temp****"
rm -rf ./crm/work/*
  echo -e "****KO crm work****"
rm -rf ./crm/webapps/crmsystem/
  echo -e "****KO crm apps****"

sleep 3

rm -rf ./crm/webapps/crmsystem.war
  echo -e "****KO crm.war*****"


echo -e "*************delete log********"
rm -rf /opt/bosslog/*
rm -rf /opt/crmlog/*
rm -rf /opt/buylog/*
