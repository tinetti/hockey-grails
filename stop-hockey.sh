#!/bin/bash

PID=`pidof java`
if [ "$PID" == "" ]; then
	echo "hockey process not found!"
	exit 1
fi

echo "stopping hockey (PID=$PID)"
kill $PID
tail -f /tmp/hockey.log
