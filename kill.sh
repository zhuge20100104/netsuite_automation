#!/bin/bash
ps -ef | grep java | awk '{print $2}' | xargs kill -9
ps -ef | grep firefox | awk '{print $2}' | xargs kill -9
ps -ef | grep chromium | awk '{print $2}' | xargs kill -9

