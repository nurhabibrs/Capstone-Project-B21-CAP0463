FROM ubuntu:20.04
RUN mkdir /api-gateway
RUN apt-get update -y && \
        apt-get install python3-pip python3 python -y

COPY . /api-gateway

WORKDIR /api-gateway
RUN pip3 install --upgrade -r requirements.txt && mkdir tmp

EXPOSE 8000

ENTRYPOINT ["python3"]
CMD ["apigateway.py"]
