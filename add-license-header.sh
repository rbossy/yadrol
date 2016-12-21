#!/bin/bash

sed -i -e '1{
h
r license-header.txt
g
N
}' "$@"
