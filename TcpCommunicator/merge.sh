#!/bin/bash
gs -dBATCH -dNOPAUSE -q -sDEVICE=pdfwrite -sOutputFile=$2 $1
