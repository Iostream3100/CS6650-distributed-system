# CS6650 Project 1: Single Server, Key-Value Store (TCP and UDP)

## Assignment Overview

In this assignment, we are asked to implement a remote key-value store. Data is stored in a hashmap.
The single-threaded server accepts requests from clients, updates the key-value store, then send
response back to clients. Both client and server supports UDP and TCP. There are threes types of
request supported: get(key), put(key, value) and delete(key). The purpose of this assignment is
letting us have a better understanding of the mechanism TCP and UDP, as well as Java socket
programming. Also, I learned how server works under the hood. Nowadays, people are familiar with
using back-end frameworks like Express, SpringBoot, but they may not have a clear understanding of
how exactly it works. This assignment helps me dive deep into the logic beneath such frameworks.
Also, designing relationships between classes also improves my oop design ability. And there are
also a lot of edge cases to deal with, like checking if an ip and port is valid, as well as handling
erroneous requests and response.

## Technical impression

I first used the single-threaded server and client from homework 1. Then I added a `Logger` class to
print and record log with current timestamp and replaced all output with logger.printLog. After
than, I implemented a key-value store class using HashMap, and created a new function to handle
request. Then I added a `Response` class with two types: success and error. For the request, I first
check if it is a valid request, if not, just log and return an error response. If the request is
valid, it is passed to the `getResponse(Request)` function to get the corresponding response. Then
the response is logged and sent to the client. Then I added a `initConfig` function to check and set
arguments for both client and server. After testing TCP server and client, I started adding UDP. I
used `DatagramPacket` to send and receive UDP packets. Then I added a timeout function for both UDP
and TCP. After that I added a pre-populate function in the client to send five request when client
starts. Finally, I built the code into jar file and tested in the command line. At that time I
notice I didn't check if the hostname or ip is valid, so I used exception handler to
handle `UnknownHostException` and shut down the service.

## How to run

### Server

- Navigate to `/res` folder
- `java -jar server.jar [port] [protocol]` to start the server, e.g. `java -jar server.jar 80 tcp`
- `Ctrl+C` to stop the server

### Client

- Navigate to `/res` folder
- `java -jar client.jar [ip or hostname] [port] [protocol]` to start the server,
  e.g. `java -jar server.jar 80 tcp`
- Enter your command. e.g. `put key value`, `get key`,`delete key`
- `Ctrl+C` to stop the client

## Examples with description

### Server

- `java -jar server.jar 80 tcp` // start tcp server on port 80
- `[2022-06-01T18:12:06.297217] TCP server running on port 80` // server running
- `[2022-06-01T18:12:11.003122] Request from </127.0.0.1>:<62036>: put a andrew` // get a put
  request from client
- `[2022-06-01T18:12:11.005541] Response to </127.0.0.1>:<62036>: [SUCCESS] null` // send response
  to client
- `[2022-06-01T18:12:11.006641] Request from </127.0.0.1>:<62037>: put b boy` // get a put request
  from client
- `[2022-06-01T18:12:11.006950] Response to </127.0.0.1>:<62037>: [SUCCESS] null` // send response
  to client

### Client

- `java -jar client.jar localhost 80 tcp` // start tcp client to send request to localhost port 80
- `[2022-06-01T18:12:10.951583] TCP client started` // client running
- `[2022-06-01T18:12:10.969483] [Pre-populate] put a andrew` // send a put request
- `[2022-06-01T18:12:11.005860] [SUCCESS] null` // get a success response
- `[2022-06-01T18:12:11.005987] [Pre-populate] put b boy` // send a put request
- `[2022-06-01T18:12:11.006929] [SUCCESS] null` // get a success response

## Assumption

- Key and value are string

## Limitation

- Only support single thread

## Citation

- [Check if ip is valid](https://www.techiedelight.com/validate-ip-address-java/)
