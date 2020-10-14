PID=$(ps -ef | grep ${jarName} | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo ${jarName} is already stopped
else
    echo kill $PID
    kill $PID
fi