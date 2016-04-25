# Create a Docker

## Create

Docker's are **build** automatically by reading the instructions from a **Dockerfile**

> **Dockerfile** -->Text Document 


    FROM ubuntu
    MAINTAINER Kimbro Staken

    RUN apt-get install -y python-software-properties python
    RUN add-apt-repository ppa:chris-lea/node.js
    RUN echo "deb http://us.archive.ubuntu.com/ubuntu/ precise universe" >> /etc/apt/sources.list
    RUN apt-get update
    RUN apt-get install -y nodejs git
    RUN npm install -g docpad@6.44

    CMD ["/bin/bash"] 
    
**FROM**
> The **FROM** instruction sets the Base Image for subsequent instructions. As such, a valid Dockerfile must have FROM as its first instruction. 

**RUN**

>The **RUN** instruction will execute any commands in a new layer on top of the current image and commit the results. The resulting committed image will be used for the next step in the 

>apt-get or yum 

**CMD**

>The CMD instruction can be used to run the software contained by your image, along with any arguments

## images

Docker images can be downloaded from docker hub. [6](https://hub.docker.com/)


## More Info

https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/