#!/usr/bin/python

from sys import argv, stdin
import re

HEADER = re.compile('^(?P<level>[#]+)\s+(?P<title>.*)$')

print '<h2>Table of Contents</h2>'
for line in stdin:
    line = line.strip()
    m = HEADER.match(line)
    if m is None:
        continue
    level = len(m.group('level')) - 1
    title = m.group('title')
    print '<div class="toc toc-level-%d"><a href="#%s">%s</a></div>' % (level, title.lower().replace(' ', '-'), title)
