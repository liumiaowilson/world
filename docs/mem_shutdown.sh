#!/bin/bash
kill -9 `ps aux | grep catalina | grep java | awk '{ print $2 }'`
