#!/bin/bash

pid=`cat SRV.PID`

if [ "$pid" == "" ]; then
  echo Service not started.
else
  echo Service PID=$pid
  `kill -3 $pid`
  echo Threads dumped.
  `kill -9 $pid`
  echo Service $pid stopped.
  rm SRV.PID
fi
