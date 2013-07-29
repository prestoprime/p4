#!/bin/bash
FILES=*.xml
for f in $FILES
do
  echo "Ingesting SIP: $f"
  echo ""
  cmd=`curl -k -H "userID: $1" -F "sipFile=@$f" https://p4.eurixgroup.com/p4ws/wf/execute/$2`
  echo $cmd
done
