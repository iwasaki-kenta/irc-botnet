# Java IRC Botnet PoC

A Java IRC Botnet PoC project I made on 2010-09-05 when I was researching about the foundations behind computer malware and the IRC network protocol.

This IRC bot connects a client to an IRC server through raw TCP socket packets, and enables the host of the IRC server to manipulate the client to his will. While the bot is running and remains connected to the IRC server, the IRC host may send commands that will initiate a DDoS attack, or will upload/execute any files on the client's PC. Every single client will be represented with their respective OS, unique identifier, country initials, and PC username.

* All IRC Hosts must have a  '@' tag before their nickname so that bots may acknowledge that a particular user in an IRC channel is the IRC server host. This is to ensure that nobody else may simply connect to an IRC server and take control of a host's clients.

This project is entirely for research purposes only, and was not made for any sort of malicious purposes whatsoever; take it as an educational interest I had back then where I wanted to see the potential of creating malware through using an IRC server as a botnet command-and-control network. I was also interested in seeing how I can implement an entire network protocol through raw packets, as there are far too many Java IRC libraries out there with a lot of excess boilerplate code.

## Features

* A completely raw and extremely fast IRC Protocol Implementation in Java.
* 4 strong implementations of Distributed-Denial-of-Service Attacks in Java.
  * Slowloris Attack
  * HTTP Attack
  * TCP Attack
  * UDP Attack
* Upload/execute files on a client's computer.
* Force a client's computer to open a link or website.
* Adds itself to the list of executables to be started up upon boot.
* Cross-compatible with three major Operating Ssystems (Linux, Mac, Windows).
* Contains an utility that uses a Java exploit to read/write directly from Windows registry.
* JVM behind the bot is persistent and un-closable due to an alternative 'loader' which re-runs the bot upon process termination.


