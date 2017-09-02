#!/bin/bash

pid=`cat SRV.PID`

CLASSPATH=$CLASSPATH:lib/*

if [ "$pid" == "" ]; then
  PARAM="-Dmnp_restapi -Xmx1536m -cp "$CLASSPATH" com.eyeline.mnp.web.RestServer"
  nohup java $DEBUG $PARAM 1>logs/out 2>logs/err &
  echo Service successfully started.
  echo $!>SRV.PID
else
  echo Service already started. PID=$pid
fi  
