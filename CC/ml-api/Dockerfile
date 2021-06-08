FROM ubuntu:20.04

COPY requirements.txt /tmp/

RUN apt-get update -y && apt-get install python3-pip python3 python -y && mkdir /ml-app && mkdir /ml-app/tmp && pip3 install --upgrade -r /tmp/requirements.txt && pip3 install tensorflow==2.5.0 && pip3 install keras==2.4.3 && pip3 install pillow && pip3 install opencv-python && apt-get install -y python3-opencv

COPY . /ml-app

WORKDIR /ml-app

 

EXPOSE 9000

ENTRYPOINT ["python3"]
CMD ["mlapi.py"]
