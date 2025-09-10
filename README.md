# Authcode-changed-consumer

## Description

A Kafka consumer for events where the authentication code is changed or cancelled
On the WebFiling side, a Companies House administrative user will have the ability to cancel an company’s authentication code. 

- [High Level Design](https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5523734600/Authentication+Code+Cancellation+High+Level+Design)

## Prerequisites
* [Git](https://git-scm.com/downloads)
* [Java](http://www.oracle.com/technetwork/java/javase/downloads)
* [Maven](https://maven.apache.org/download.cgi)
* [Apache Kafka](https://kafka.apache.org/)

## Environment variables

| Variable                                  | Description                                                           |
| ----------------------------------------- | --------------------------------------------------------------------- |
| CHS\_INTERNAL\_API\_KEY                   | Internal API key used to authenticate with CHS internal endpoints     | 
| ACCOUNT\_URL                              | Base URL for the Accounts API                                         | 
| BOOTSTRAP\_SERVER\_URL                    | Kafka broker bootstrap servers                                        | 
| CHS\_KAFKA\_ASSOCIATION\_ITEMS\_PER\_PAGE | Page size when fetching CHS association items                         |
| AUTHCODE\_CANCELLATION\_TOPIC             | Kafka topic for auth code cancellation messages                       |
| KAFKA\_ERROR\_TOPIC                       | Kafka error/dead-letter topic for failed auth code cancellations      | 
| AUTHCODE\_CANCELLATION\_GROUP\_ID         | Kafka consumer group ID for auth code cancellation consumer           | 
| MAX\_ATTEMPTS                             | Number of times a message is retried before being sent to error topic |
| BACKOFF\_DELAY                            | Delay between retries in milliseconds                                 | 

## Getting Started
To set up and build the service, follow these steps

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the
   README.
2. Get the broker up by running ` chs-dev services enable authcode-changed-consumer
   ` in your terminal from the docker-chs-development directory. 
3. Run `chs-dev up` in terminal from the docker-chs-development directory.
4. Run `chs-dev status` to confirm its running
