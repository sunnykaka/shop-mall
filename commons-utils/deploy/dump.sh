#solr dump shell
filePath=/home/Play/sh/dump.lock
#不同环境下需修改url
solr_host=http://localhost:8038
function swap_index(){
    #不同环境下需修改url
    curl 'http://wwww.yijushang.net/forbidDump'  
    if [ -f $filePath ];
	then
     	status=`cat $filePath`
		if [ $status -eq 1 ];
		then
		   curl $solr_host'/solr/admin/cores?action=SWAP&core=product&other=product_w' >> /home/Play/sh/swap.log
		fi
	else
		echo "$filePath not exsit"
        #create lock file
		echo "0">$filePath
	fi
	return 0;
}

function do_dump(){
 
 echo "0">$filePath
 #delete all data
 curl $solr_host'/solr/product_w/update/?stream.body=<delete><query>*:*</query></delete>&stream.contentType=text/xml;charset=utf-8&commit=true' >> /home/Play/sh/delete.log
 echo "full-dump request"
 curl $solr_host'/solr/product_w/dataimport?command=full-import&debug=false&commit=true' >> /home/Play/sh/dump.log
 if [ $? -eq 0 ];
 then
	echo "1">$filePath
 fi
 return 0;

}
# swap read/write
swap_index;
do_dump;





