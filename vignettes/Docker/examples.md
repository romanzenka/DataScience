# Examples

## Udacity-Tensor Flow


    docker run -p 8889:8888 -v /Users/m112447/Desktop/docker/:/notebooks -it --rm b.gcr.io/tensorflow-udacity/assignments:0.5.0


`-v /Users/m112447/Desktop/docker/:/notebook` This will forward my local folder(*/Users/m112447/Desktop/docker/*) to the docker one (*/notebooks*)


Just Download the Udacity examples form this [repo](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/udacity) to the local folder. 


## Keras (Python Library for Deep Learning)

    sudo docker run --rm  --device /dev/nvidia0:/dev/nvidia0 --device /dev/nvidia-uvm:/dev/nvidia-um -it --privileged -v /home/MFAD/m112447/research/workspace/:/workspace/ --name keras slowvak/keras-cuda-c9
    
   
   
## Save an Image and Load it Back 

This might be useful in case of the udacity course (Code can be developed and then save the whole image of the docker and share).
___________________________

    $ docker ps -a
    $ docker images
    $ docker save -o image.tar
    $ docker load < image.tar
 
## Run Docker interactive mode 

    docker run --rm -it debian /bin/bash

    
## Fun

Docker that runs full chrome version. 
________________________    
___
    docker run -it \
        --net host \ # may as well YOLO
        --cpuset-cpus 0 \ # control the cpu
        --memory 512mb \ # max memory it can use
        -v /tmp/.X11-unix:/tmp/.X11-unix \ # mount the X11 socket
        -e DISPLAY=unix$DISPLAY \ # pass the display
        -v $HOME/Downloads:/root/Downloads \ # optional, but nice
        -v $HOME/.config/google-chrome/:/data \ # if you want to save state
        --device /dev/snd \ # so we have sound
        --name chrome \
        jess/chrome
