rm devDb.*

if [ -f "hockeyDb.log" ]; then
	cp hockeyDb.log devDb.log
fi

if [ -f "hockeyDb.properties" ]; then
	cp hockeyDb.properties devDb.properties
fi

if [ -f "hockeyDb.script" ]; then
	cp hockeyDb.script devDb.script
fi


