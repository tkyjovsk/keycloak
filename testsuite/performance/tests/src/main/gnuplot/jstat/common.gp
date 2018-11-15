set datafile separator whitespace
set datafile commentschar ""
set xlabel "Runtime in Seconds"
set ylabel "Memory in kB"
set terminal pngcairo size 1280,800
set xtics rotate
set yrange [0:*]
set key below
set grid
