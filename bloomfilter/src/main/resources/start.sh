#!/bin/sh
java -Dcom.sun.management.jmxremote=true                \
     -Dcom.sun.management.jmxremote.port=8178           \
     -Dcom.sun.management.jmxremote.authenticate=false  \
     -Dcom.sun.management.jmxremote.ssl=false           \
     -jar bloomfilter-0.0.1-SNAPSHOT.jar &
