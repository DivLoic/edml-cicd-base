FROM openjdk:8-jdk
MAINTAINER Loïc DIVAD <ldivad@xebia.fr>

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && apt-get install -y python3.5 python3-dev python3-pip curl
RUN apt-get update -y && apt-get install -y apt-utils dialog software-properties-common

RUN echo "deb http://packages.cloud.google.com/apt cloud-sdk-$(lsb_release -c -s) main" \
  | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

RUN cat /etc/apt/sources.list.d/google-cloud-sdk.list
RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg --output - | apt-key add -
RUN apt-get update -y && apt-get install -y google-cloud-sdk kubectl

RUN ln -sf python3 /usr/bin/python
RUN ln -s pip3 /usr/bin/pip

RUN pip3 install --no-cache --upgrade pip
RUN pip3 install --no-cache --upgrade setuptools wheel tensorflow==1.15

RUN echo '#!/bin/bash\njava -jar /usr/share/edml/cicd-base.jar "$@"' > /usr/bin/edml && chmod +x /usr/bin/edml

ARG JAR_FILE
ARG CLASSPATH

ADD target/${CLASSPATH} /usr/lib/edml/
ADD target/${JAR_FILE} /usr/share/edml/cicd-base.jar