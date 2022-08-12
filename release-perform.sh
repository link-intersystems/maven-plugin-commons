#!/usr/bin/env bash

mvn release:perform -P release -DlocalCheckout=true -Darguments="-Dmaven.site.skip=true -Dmaven.site.deploy.skip=true"