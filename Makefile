JSVC := $(shell which jsvc)
PWD  := $(shell pwd)

target/slack-slurper-0.2.0-SNAPSHOT-standalone.jar:
	lein uberjar

start: target/slack-slurper-0.2.0-SNAPSHOT-standalone.jar
	sudo $(JSVC) -java-home "$$JAVA_HOME" \
	-cp "$(PWD)/target/slack-slurper-0.2.0-SNAPSHOT-standalone.jar" \
	-outfile "$(PWD)/out.txt" \
  -errfile "$(PWD)/err.txt" \
	-debug \
	-jvm server \
	-wait 80000 \
	slack_slurper.core

stop:
	sudo $(JSVC) -java-home "$JAVA_HOME" \
	-cp "$(PWD)/target/slack-slurper-0.2.0-SNAPSHOT-standalone.jar" \
	-stop \
	-debug \
	-jvm server \
	slack_slurper.core


#	-DSLACK_SLURPER_TOKEN="$$SLACK_SLURPER_TOKEN" \
