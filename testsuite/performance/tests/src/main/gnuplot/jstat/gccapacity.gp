set title "jstat -gccapacity"
plot for [i in "NGC S0C S1C EC OGC OC MC CCSC"] datafile using 1:i title columnheader(i) with lines
