# Scala Facebook API Simulator
For University of Florida COP5615 (Fall 2015).
By Chelsea Metcalf and Grant Hernandez.

Accordion - Project 3 for COP5615 by Grant Hernandez (no group)
===============================================================
Accordion - A Scala Chord Simulator
   by Grant Hernandez

usage: accordion [numNodes] [numRequests]
  numNodes is a positive integer
  numRequests is the number of requests that each node will execute

Example:
  sbt "run 50 10" # runs a 50 node chord simulation where each node does 10 requests

Example run
===========
projectCode/ $ sbt "run 50 10"
[info] Running Accordion 50 10
Accordion - A Scala Chord Simulator
   by Grant Hernandez

Starting Chord simulation with 50 nodes and 10 requests per node
[INFO] [10/26/2015 21:22:32.299] [chord-server-akka.actor.default-dispatcher-2] [akka://chord-server/user/node0-100d81aafe637717] Initial chord node started
Round 1 (avg. hops 0.00, alive 50)
Round 2 (avg. hops 0.00, alive 50)
Round 3 (avg. hops 18.94, alive 50)
Round 4 (avg. hops 22.09, alive 50)
Round 5 (avg. hops 23.97, alive 50)
Round 6 (avg. hops 23.48, alive 50)
Round 7 (avg. hops 23.88, alive 50)
Round 8 (avg. hops 21.12, alive 50)
Round 9 (avg. hops 18.59, alive 50)
Round 10 (avg. hops 16.71, alive 50)
Round 11 (avg. hops 15.26, alive 50)
Simulation stopped
Average hops per lookup: 14.09
O(log n): 5.64

Output Explanation
==================
Every second each node will perform a request, if it is has successfully
joined to the network. The Chord simulation manager will once a second query
all of the nodes for stats: what the current node average hop for lookups and
if it's "alive" (joined and not crashed)

As you can see from the output, the avg. hops decreases with time as the
nodes' finger tables start to converge to an optimal network

At the end the true average hops is printed and the O(log n) bound is printed
as well for reference (constant of 1)

File Structure
==============
projectCode/ - IntelliJ IDEA project root and SBT build root (code)
README.txt - this file

What is Working
===============
  * Chord joining and lookups

Largest network
==================
  * About 5000 before my computer couldn't handle it

Bonus
=====
Not done

Build Environment
=================
Java SDK 1.8
Scala SDK 2.11.7
SBT version 0.13.8

Build Tool
==========
This project uses sbt for dependency management, building, and running. To run
this project, type `sbt run` in the projectCode/ directory (the same directory
as build.sbt).

In order to pass command line arguments to Accordion, you may use the form `sbt
"run <ARGS>"`, where <ARGS> is matches the below usage statement.
Note the double quotes for passing arguments.

