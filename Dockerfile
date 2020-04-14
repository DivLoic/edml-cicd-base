FROM openjdk:8-jdk
MAINTAINER Loïc DIVAD <ldivad@xebia.fr>

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && apt-get install -y python3.5 python3-dev python3-pip curl gettext-base
RUN apt-get update -y && apt-get install -y apt-utils dialog software-properties-common bc

RUN echo "deb http://packages.cloud.google.com/apt cloud-sdk-$(lsb_release -c -s) main" \
  | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

RUN cat /etc/apt/sources.list.d/google-cloud-sdk.list
RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg --output - | apt-key add -
RUN apt-get update -y && apt-get install -y google-cloud-sdk kubectl

RUN ln -sf python3 /usr/bin/python
RUN ln -s pip3 /usr/bin/pip

RUN pip3 install --no-cache --upgrade pip
RUN pip3 install --no-cache --upgrade virtualenv setuptools wheel

ADD bin/edml.sh /usr/bin/edml
ADD bin/maven-eval.sh /usr/bin/maven-eval
ADD bin/export-model-path.sh /usr/bin/export-model-path
ADD bin/export-model-version.sh /usr/bin/export-model-version

ARG JAR_FILE
ARG CLASSPATH

ADD target/${CLASSPATH} /usr/lib/edml/
ADD target/${JAR_FILE} /usr/share/edml/cicd-base.jar