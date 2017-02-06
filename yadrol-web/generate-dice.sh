#!/bin/env bash


DICE_DIR=src/main/webapp/icons/dice-collections/mixed

for dt in 4 6 8 10 12 20; do
    matrix=${DICE_DIR}/d${dt}_X.svg
    echo Matrix: $matrix
    for ((n=1;n<=$dt;++n)); do
	fn=${DICE_DIR}/d${dt}_${n}.svg
	echo '  'Generating: $fn
	if [ "$dt" = "10" -a "$n" = "10" ]; then
	    m=0
	else
	    m=$n
	fi
	xsltproc --stringparam n $m fill-dice-number.xslt $matrix >$fn
    done
done

