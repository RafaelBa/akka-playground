Notes Application
==================
This application consist of six parts: 
1. Portier Service
2. Notes Service
3. Authentication Service
4. RabbitMQ - storing note requests
5. MongoDB - storing notes
6. MySQL - storing user information

Portier 
---------
The Portier is taking care of people authenticating themselves, giving them a token, so they can proceed with any action that is only alowed by logged in users. 
It will also check the user's token once they do a request. After confirming that the user is authorized to do the action it will forward the request. 
Because this is a playground and I wanted to bring RabbitMQ to use the create request will be pushed down the queue and the user just gets back a token. With this token they can either get their note or the status of the process. 

Notes Services
-----------------
This service listens to RabbitMQ, reads the request from it and stores them into MongoDB. It also resolves the data needed and enriches the request to be a full note. 
It also retrieves notes from the database and responds with them to whoever asks. 

Authentication Service
-------------------------
This service checks if username and password match and responds with a token as sign of validity. It also checks provided tokens, if they are valid and the user is still considered logged in. 
The service is connected to MySQL database which contains login data and personal information about users. 

Running
-------
This project is build to rely on Docker. Every instance itself runs on Docker and autonomous, but can also be run with docker-compose as a whole application.

Motiviation
-----------
The selection of databases is purely random as the goal of this project is to see how to connect to a relational database, a NoSQL database and a queue with mostly using Akka technologies.  


