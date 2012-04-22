#!/bin/bash
find ./src -type f | xargs wc -l | cut -c 1-8 | awk '{total += $1} END {print total}'
