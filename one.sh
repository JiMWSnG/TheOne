#! /bin/sh
java -Xmx1536M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
