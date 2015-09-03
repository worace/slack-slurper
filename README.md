# slack-slurper

A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


### Daemonization

Based on [this article](http://www.rkn.io/2014/02/06/clojure-cookbook-daemons/), using the Apache Commons Daemons library.

Just using `init.d` with the following init conf in
`/etc/init/slack-slurper.conf`:

```
description "Start slack slurper websocket daemon"
author "Horace Williams"
env SLACK_SLURPER_TOKEN=your-token-here
start on runlevel [2345]
stop on shutdown
exec java -jar /root/slack-slurper.jar
```

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
