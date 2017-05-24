.. _use_cases:

Use Cases
*********

Use cases examples were taken from `Kafka Use Cases <http://kafka.apache.org/uses>`_,
`Amazon Kinesis Use Cases <https://aws.amazon.com/kinesis/streams/>`_ and
`this blog about IoT <http://wiprodigital.com/2016/04/07/kafka-as-a-message-broker-in-the-iot-world-part-2/>`_

Messaging
=========

Streaming platforms works well as a replacement for a more traditional message broker. Message brokers are
used for a variety of reasons (to decouple processing from data producers, to buffer unprocessed messages,
etc). In comparison to most messaging systems Kafka has better throughput, built-in partitioning,
replication, and fault-tolerance which makes it a good solution for large scale message processing applications.

In our experience messaging uses are often comparatively low-throughput, but may require low end-to-end
latency and often depend on the strong durability guarantees Kafka provides.

In this domain the DXL Streaming Platform is comparable to traditional messaging systems such as
`ActiveMQ <http://activemq.apache.org/>`_ or `RabbitMQ <https://www.rabbitmq.com/>`_.


Website Activity Tracking
=========================

The original use case for streaming platforms was to be able to rebuild a user activity tracking pipeline as
a set of real-time publish-subscribe feeds. This means site activity (page views, searches, or other actions
users may take) is published to central topics with one topic per activity type. These feeds are available for
subscription for a range of use cases including real-time processing, real-time monitoring, and loading
into Hadoop or offline data warehousing systems for offline processing and reporting.

Activity tracking is often very high volume as many activity messages are generated for each user page view.


Metrics
=======

Streaming platforms are often used for operational monitoring data. This involves aggregating statistics
from distributed applications to produce centralized feeds of operational data.


Log and Event Aggregation
=========================

Many people use streaming platforms as a replacement for a log aggregation solution. Log aggregation
typically collects physical log files off servers and puts them in a central place (a file server or HDFS
perhaps) for processing. Kafka abstracts away the details of files and gives a cleaner abstraction of log or
event data as a stream of messages. This allows for lower-latency processing and easier support for multiple
data sources and distributed data consumption. In comparison to log-centric systems like Scribe or Flume,
thr DXL Streaming Platform offers equally good performance, stronger durability guarantees due to replication,
and much lower end-to-end latency.


Stream Processing
=================

Many users of streaming platforms process data in processing pipelines consisting of multiple stages, where
raw input data is consumed from topics and then aggregated, enriched, or otherwise transformed into new
topics for further consumption or follow-up processing. For example, a processing pipeline for recommending
news articles might crawl article content from RSS feeds and publish it to an "articles" topic; further
processing might normalize or deduplicate this content and published the cleansed article content to a new
topic; a final processing stage might attempt to recommend this content to users. Such processing pipelines
create graphs of real-time data flows based on the individual topics.


Event Sourcing
==============

`Event sourcing <http://martinfowler.com/eaaDev/EventSourcing.html>`_ is a style of application design where
state changes are logged as a time-ordered sequence of records. The DXL Streaming Platform support for very
large stored log data makes it an excellent backend for an application built in this style.


Commit Log
==========

The DXL Streaming Platform can serve as a kind of external commit-log for a distributed system. The log helps
replicate data between nodes and acts as a re-syncing mechanism for failed nodes to restore their data.
The log compaction feature in the DXL Streaming Platform helps support this usage. In this usage Kafka is
similar to `Apache BookKeeper <http://zookeeper.apache.org/bookkeeper/>`_ project.


Real-time Analytics
===================

You can have your applications run real-time analytics on high frequency event data such as sensor data
collected by the DXL Streaming Platform, which enables you to gain insights from your data at a frequency of
minutes instead of hours or days.


IoT Applications
================

In the IoT world, there is massive amount of data coming from “things” (devices and sensors), which send
data in real-time. There are three major steps in processing voluminous sensor data at scale: data ingestion,
data storage and data analytics. A big concern is how these massive amounts of data coming from all of the
IoT systems can be stored and analyzed in a timely manner, without losing any information.

Basically, at each layer, there is a need to have some kind of broker that maintains a balance between the
data produced and data consumed. For example, if there are millions of devices sending data at regular
intervals (which could be even sub-second intervals) to an IoT platform, the platform needs to buffer the
data before it can process it. If there are multiple consumers for the device data, how do we connect the
data to the right channel for the right kind of analysis?

In fact, there could be multiple consumers needing the same data for a variety of purposes. In some sense,
this data can be treated as similar to the enormous event data from social networking sites and e-commerce
sites where the data from multiple places is received at high velocity and volume to be processed and analyzed.

DXL Streaming Platform’s features make it useful for IoT applications.
IoT applications will find the DXL Streaming Platform a very important part of architecture due to the
performance and distribution guarantees that it provides. Along these lines, Kafka will also be useful for
sending data from devices that can be aggregated in the form of files, or data being sent at a very high
frequency. In this case, the producers could be device agents sending data at high frequencies, or device
gateways/aggregators aggregating and collecting data from multiple devices in the form of files. The
consumers need not consume the messages at the same rate at which the data is published. Also,
real-time pipelines can be created by using the DXL Streaming Platform in association with stream-processing
engines to process the data as it is coming from the device/gateway.
