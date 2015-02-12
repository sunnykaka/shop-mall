#! /bin/sh
#tomcat重启脚本
#
RETVAL=0

start() {
  echo -n "Tomcat Starting..."
echo
 /opt/appserver/boss/bin/startup.sh
sleep 3
/opt/appserver/buy/bin/startup.sh
sleep 3
/opt/appserver/crm/bin/startup.sh
sleep 3
/opt/appserver/bug/bin/startup.sh
}

stop() {
  echo -n "Tomcat Stop..."

sleep 3
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

}


restart() {
 echo -n "Tomcat restart..."
 echo
 stop
 start
}

case "$1" in
 start)
      start;;
 stop)
   stop;;
 restart)
      restart;;
 *)
    echo $"Usage: $0 {start|stop|restart}"
 exit 1
esac

exit $RETVAL




