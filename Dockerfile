FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y curl wget gnupg software-properties-common git

# Amazon JDK
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add - 
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN apt-get update && \
    apt-get install -y java-11-amazon-corretto-jdk

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update && \
    apt-get install -y sbt

RUN mkdir /volume && \
    git clone https://github.com/SkiTko/TwitterImageDownloader.git && \
    cd TwitterImageDownloader && \
    sbt compile

WORKDIR /TwitterImageDownloader

CMD [ "sbt", "run" ]
