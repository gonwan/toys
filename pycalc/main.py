#!/usr/bin/python
import pycalc
import sys

if len(sys.argv) == 1:
    pycalc.Parser.run()
elif len(sys.argv) == 2:
    pycalc.Parser.calc(sys.argv[1])
else:
    print('ERROR: invalid arguments...')
