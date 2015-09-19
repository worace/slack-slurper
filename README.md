## SlackSlurper

Experiment to see if it's possible to slurp and archive
messages from a slack org so we can search them beyond
the free tier expiration period

So far it:

* Connects to the Slack RTM API using websockets
* Heartbeat the connection using Ping messages to keep
it alive
* Writes all messages to a log file
* Supports processing log files to push all
messages into ElasticSearch.

For searching, see related [Slack Searcher](https://github.com/worace/slack-searcher)
project which attempts to give a super basic web interface
for searching the messages.

### TODOs

* [ ] Auto-reconnect on websocket connection closing (even
with the heartbeat we sometimes lose connection and should just restart it)
* [ ] Publish the messages to ES in real time as they come in in addition
to logging them
* [ ] Filter out only "message" type messages (lots of other stuff appears
including join/leave notices etc). Try to use a manifold stream transducers for this.

__Namespaces__

* core - startup / shutdown; hosting repl server; invokes logging config
* connection - deals with opening connection to slack
* heartbeat - takes a stream and pings it periodically
* logging - configs logging
* listener - take a stream and invoke func on messages
that come in
* filters - select specific types messages off a stream
* indexing - ES connections and publishing

### Deployment / Daemonization

Currently just running it on a DigitalOcean VPS.

Uses `init.d` with the following init conf in
`/etc/init/slack-slurper.conf`:

```
description "Start slack slurper websocket daemon"
author "Horace Williams"
env SLACK_SLURPER_TOKEN=your-token-here
start on runlevel [2345]
stop on shutdown
exec java -jar /root/slack-slurper.jar
```

TODO: Would be interesting to investigate using Monit or another tool
to get more interesting status information on the process.

### Random Notes

__Message Fields__

* type
* channel
* user
* text
* ts
* team

example:

```
;; {"type":"message","channel":"C02Q114D3","user":"U02QCFVPS","text":"haha ^","ts":"1440708033.000049","team":"T029P2S9M"}
;; index -- text, user, channel
;; message id -- channel + ts combo -- ts is guaranteed unique by channel
```

__Known slack event message subtypes__

* "user_typing"
* "im_created"
* "star_removed"
* "user_change"
* "reaction_added"
* "file_change"
* "pong"
* "reaction_removed"
* "star_added"
* "emoji_changed"
* "file_public"
* "presence_change"
* "file_shared"
