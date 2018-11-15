set title "Non-heap Memory"
plot\
    datafile using 1:(column('MC')+column('CCSC')) title 'CCSC' with filledcurves x1, \
    datafile using 1:'MC' title 'MC' with filledcurves x1, \
    \
    datafile using 1:(column('MC')+column('CCSC')) notitle with lines lc rgb "#000000" lw 1, \
    datafile using 1:'MC' notitle with lines lc rgb "#000000" lw 1
