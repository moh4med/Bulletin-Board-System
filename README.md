# Bulletin-Board-System

Assignment phase 1 (TCP/IP):

Description
In this assignment we will be simulating a news bulletin board system. In this system there are several
processes accessing the system, either getting the news from the system or updating the news. The system
will consist of a server that implements the actual news bulletin board and several remote clients that
communicate with the server using the TCP/IP protocol suite. The remote clients will have to communicate
with the server to write their news to the bulletin board and also to read the news from the bulletin board.


Assignment phase 2 (RMI or RPC):
Description
In this phase you will implement news bulletin board system using RPC/RMI instead of sockets.
So, we will be developing another distributed client/server version of the solution using a serviceoriented
request/reply scheme.
Typically, we will use the same code we have written for Assignment 1 but using RMI for all
necessary communication instead of the Sockets. Also, while RMI will take care of the client threads
(we don’t need to handle it like sockets), we still need to provide the necessary synchronization. In
sum, the major difference from Assignment 1 is that Read and Write requests of clients will be
remote method invocations.
Unless otherwise specified in this document, Assignment 1 specification is valid for this assignment.
