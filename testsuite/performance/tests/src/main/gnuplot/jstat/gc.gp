set title "jstat -gc"
plot for [i in "S0C S1C EC OC MC CCSC"] datafile using 1:i title columnheader(i) with lines
