#!/usr/bin/python
# coding=utf-8

import re, sys, os, stat

file = sys.argv[1]

# Read from original json
fobj = open(file, 'r')
firstLine = fobj.readline()
fobj.close()

all = []
fobj = open(file, 'w')
needToEliminate = ['SuiteTeardown', 'SuiteSetup', 'BeforeClass', 'AfterClass']
result = firstLine

# Delete suitesetup/teardown.. cases
for setup in needToEliminate:
    n = 1
    while n < 100:
        reg = r',?{("result":)((?:(?!result).)*?)' + setup + '([^}])*},?'  # Reg to match reduntdant result
        result = re.sub(reg, '', result)
        n = n + 1

# Calculate how many pass and how many fail
p = re.findall('"result":"passed"', result)
passed = str(len(p))
print("Passed:" + passed)

f = re.findall('"result":"failed"', result)
failed = str(len(f))
print("Failed:" + failed)

# Rewrite the report according actual pass and failed
reg = r'"total_passed":.*?,'
result = re.sub(reg, '"total_passed":' + passed + ',', result)

reg = r'"total_failed":.?,'
result = re.sub(reg, '"total_failed":' + failed + ',', result)

all.append(result)
fobj.write(''.join(all))
fobj.close()
