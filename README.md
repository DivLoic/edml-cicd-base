# EDML CI/CD Base
###### Gitlab runner image the [event driven ml](https://blog.loicmdivad.com/talks/event-driven-machine-learning-xebicon19/) experiment
![EDML CI/CD Runner Workflow Generation](https://github.com/DivLoic/edml-cicd-base/workflows/EDML%20CI/CD%20Runner%20Workflow%20Generation/badge.svg)
![EDML CI/CD Base Docker Image](https://img.shields.io/docker/v/loicmdivad/edml-cicd-base?style=plastic)

## About EDML
[Event-Driven Machine Learning](https://github.com/DivLoic/event-driven-ml) is an experiment ran by 
[@DivLoic](https://github.com/DivLoic) and [@Giulbia](https://github.com/giulbia).
The idea is to bring together all the components to build a system serving prediction in realtime.
From exploring the data with interactive tools like [Ai Platform](https://cloud.google.com/ai-platform/) 
to deploying streaming application in the cloud with [Confluent Cloud](https://www.confluent.io/confluent-cloud/)
the EDML project discuss the important points leading to model serving automation.

## Why?
The EMDL runners serve multiple purposes, such as: packaging maven projects, creating python artifacts or submitting 
model trainings on Google Cloud AI Platform. To create a single image with all the necessary dependencies (e.g. maven,
python, gcloud SDK) this module use Github Actions and push the result to 
[docker hub](https://hub.docker.com/repository/docker/loicmdivad/edml-cicd-base).
In addition, the image contains a few scripts carrying the CI logic of project. It helps write the Gitlab CI stages
for each module from [event-driven-ml](https://github.com/DivLoic/event-driven-ml).  

## How?

## See also
- [EDMl at Xebicon'19](https://youtu.be/g646cjDvg84)
- [EDML: Event-driven ml slides (2019)](https://speakerdeck.com/loicdivad/event-driven-machine-learning)
- [EDMl on Confluent Webinar](https://www.confluent.io/online-talks/event-driven-machine-learning-avec-publicis-sapient/)
- [EDML: Event-driven ml slides (2020)](https://speakerdeck.com/giulbia/event-driven-machine-learning-6822798c-54ea-4db3-b348-b536b3ec5d9e)
