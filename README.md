Initial Countdown server
-----------------------
A project to manage countdown timers on the web. Users can view timers that are collected all across the web, and can also add their own timers and share it with friends.

Cloning
-------
Now that we have a submodule, the cloning is no longer a one command operation. Please remember that after cloning, you need to:

       git submodule init
       git submodule update

If we find these submodules create too much headaches for us then we can discuss and remove it.

Running
-------

1. Get sbt 0.11.0
2. Install MongoDB

Then excute, at the root level:

        sbt update
        sbt compile
        sbt run


This will read some release date for games, persist it to mongo and start a jetty service.

Javascript client
-----------------
The JS is available at http://localhost:55555/static. For now, it simplay displays all the countdown timers that are loaded in the countdown system.

Data
----
Calling http://localhost:55555/countdownlist should give something like:
{"countdowns":[{"label":"Kingdoms of Amalur: Reckoning","url":"4ed54653a8b6be639489b0ac"},{"label":"Another World","url":"4ed54653a8b6be639489b0ad"},{"label":"Syndicate Executive Package","url":"4ed54653a8b6be639489b0ae"}]}

To access a countdown, use one of the urls, like: http://localhost:55555/countdown/4ed54653a8b6be639489b0ad
It should display something like: 

{"name":"Another World","url":"4ed54653a8b6be639489b0ad","eventDate":1328047200000}


