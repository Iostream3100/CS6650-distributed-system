# CS6650 Project 4: Multi-threaded Key-Value Store using RPC

## Assignment Overview

In this assignment, we are asked to implement a multi-threaded fault-tolerant remote key-value store using Remote Procedure Calls (RPC). Data is stored in a concurrency hashmap. The coordinator uses PAXOS to keep the data consistent among all the servers. The server registers the key-value store using RMI, and
clients can call the key-value store remotely. When a server receives a put or delete request, it calls the proposer to start the PAXOS procedure, and the proposer will ask all acceptors to promise on an id. If a majority of acceptors promised the id, the consensus is reached. There are threes types of requests supported: get(key), put(key, value) and delete(key). The purpose of this assignment is to let us have a better understanding of the mechanism of RPC and RMI. Also, I learned how a server works under the hood.
Nowadays, people are familiar with using back-end frameworks like Express, SpringBoot, but they may
not have a clear understanding of how exactly it works. This assignment helps me dive deep into the
logic beneath such frameworks. Also, designing relationships between classes improves my oop design
ability. And there are also a lot of edge cases to deal with, like checking if an ip and port is
valid, as well as handling erroneous requests and response.

## Technical impression

I first used the RMI server and client from project 3. Then I implemented the proposer, the acceptor and the learner. The proposer can start the PAXOS, ask for all accptors to promise an id and propse a value to all proposer and learner. The leaner is responsible for updating the key-value store and send the data back to the client. After that, I added a CommandLineHandler class that can interact with user to connect servers or set an acceptor to be failed in phase 1 or phase 2. Also, I let the acceptor to sleep for some time before returning the promise for an id, so level 0 a and b can be more easily tested.
In addition, key steps are logged for servers to recovery from crash.  Lastly, I tested and built it into jar files.

## How to run

- Navigate to `/src` folder

- `javac -jar server.jar [port] [id]` to start the server with port and a unique id

  - `addall [server port1] [server port2] ....` to add server i's acceptor and learner to server j's proposer; e.g: add 3100 3200 3300 3400 3500 will connect all  servers(only need to enter this command once to connect all servers)
  - `up/down 1/2` to set this servers acceptor to fail/normal in phase 1/2
  - `svcnt` print total number of servers added to this server

- `javac -jar client.jar [server hostname or ip] [server port]` to connect to the server

  - `put [key] [value]` to put a key-value pair
  - `delete [key]` to delete a key-value pair
  - `get [key]` to get the value of a key

- `Ctrl+C` to stop the process

- Steps:

  1. Start all 5 servers
  2. Enter `addall [server1 port].....[server 5 port]` to connect all servers

  3. Start the client

## Examples with description

- start all servers
  ![](res/screenshots/1-1.png)

- connect all servers

  ![](res/screenshots/1-2.png)

- start two clients to connect server1 and server2

  ![](res/screenshots/1-3.png)

- `put x xyz` in client 1 and `get x` in client 2

  ![](res/screenshots/1-4.png)

- Level 0 a and level 0 b: `put r client1` at client1 and `put r rclient2` at client2 at the same time, but only the first request got accepted, second failed.

  ![](res/screenshots/1-5.png)

- Set server 1 and server 2's acceptor to be failed in phase 1

  ![](res/screenshots/1-6.png)

- Level 1: 3 of 5 servers promised the propose, consensus was reached. 

- ![](res/screenshots/1-7.png)

- Set server 1 and server 2's acceptor back to normal  in phase 1, then set server 1 and server 2's acceptor to be failed in phase 2

  ![](res/screenshots/1-8.png)

- Consensus is reached.

  ![](res/screenshots/1-9.png)

  

## Assumption

- atmost n (n < majority) acceptors will fail

## Limitation

- Key and value are string
- Processing time for put and delete request is long

## References

- [Concurrent Hash Map](https://www.javatpoint.com/hashmap-vs-concurrenthashmap-in-java)
