#!/bin/bash
find ./src "*.java" | xargs wc -l | cut -c 1-8 | awk '{total += $1} END {print total}'
