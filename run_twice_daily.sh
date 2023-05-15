#!/bin/bash

# Set the path to the Java executable
JAVA_EXECUTABLE="/usr/bin/java"
CLASS_PATH="/Users/Yoni/Documents/Yewbdar/Projects/src/automations/cmv/BuySellCMV.class"

#run in 6 hrs , Just incase you never know :) you might get lucky
EXECUTION_INTERVAL=21600

while true; do
    # Run while caffinate , foces the pc to be alert
     caffeinate -s $JAVA_EXECUTABLE -cp ".:lib/*" BuySellCMV
    
    sleep $EXECUTION_INTERVAL
done
