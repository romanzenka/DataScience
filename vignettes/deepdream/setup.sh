#!/bin/bash -e

echo Clone caffe
git clone git@github.com:BVLC/caffe.git

echo Clone deepdream ipython notebook
git clone git@github.com:google/deepdream.git

echo Get the data for the googlenet model
(cd /caffe/models/bvlc_googlenet ; curl -L -O -C http://dl.caffe.berkeleyvision.org/bvlc_googlenet.caffemodelcurl )

# Now here comes the hard part - configure and install caffe, pycaffe and ipython
# Caffe - follow instructions here http://caffe.berkeleyvision.org/installation.html
# Python - Anaconda (comes with dependencies)
# CUDA for GPU acceleration

cd caffe
cp Makefile.config.example Makefile.config
# Adjust Makefile.config (for example, if using Anaconda Python)
make all -j8
make pycaffe
make distribute
cd ..

# Do not forget to set pythonpath: export PYTHONPATH=<.>/caffe/python:$PYTHONPATH
