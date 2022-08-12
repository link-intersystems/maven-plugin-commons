#!/usr/bin/env bash

mvn -P release,compatibility release:prepare -DpushChanges=false