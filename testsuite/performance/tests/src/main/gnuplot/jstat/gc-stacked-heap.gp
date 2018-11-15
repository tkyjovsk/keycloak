set title "Heap Memory"
plot\
    datafile using 1:(column('S0C')+column('S1C')+column('EC')+column('OC')) title 'OC' with filledcurves x1, \
    datafile using 1:(column('S0C')+column('S1C')+column('EC')) title 'EC' with filledcurves x1, \
    datafile using 1:(column('S0C')+column('S1C')) title 'S1C' with filledcurves x1, \
    datafile using 1:'S0C' title 'S0C' with filledcurves x1, \
    \
    datafile using 1:(column('S0C')+column('S1C')+column('EC')+column('OC')) notitle with lines lc rgb "#000000" lw 1, \
    datafile using 1:(column('S0C')+column('S1C')+column('EC')) notitle with lines lc rgb "#000000" lw 1, \
    datafile using 1:(column('S0C')+column('S1C')) notitle with lines lc rgb "#000000" lw 1, \
    datafile using 1:'S0C' notitle with lines lc rgb "#000000" lw 1
