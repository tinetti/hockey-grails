nohup grails prod run-app > /tmp/hockey.log &

echo "Tailing log file: /tmp/hockey.log"

tail -f /tmp/hockey.log
