FROM ubuntu:20.04
RUN mkdir /ml-api
RUN apt-get update -y && \
        apt-get install python3-pip python3 python -y

COPY . /ml-api

WORKDIR /ml-api
RUN pip3 install --upgrade -r requirements.txt && mkdir tmp

EXPOSE 9000

ENTRYPOINT ["python3"]
CMD ["mlapi.py"]
