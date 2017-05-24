.. _management:

Management APIs
***************

DXL Streaming Platform components often require a different kind of administrative and setup operations to
be performed by users or administrators, either interactively or using automation tools or scripts. This
chapter describes the different management operations that can be performed and how to perform them.

Operations that can be performed include:

* Get cluster status
* List existing topics
* Create a new topic
* Describe an existing topic
* Modify an existing topic
* Re-balance topic leadership
* Reassign partitions
* Set quotas
* Describe quotas
* List ACLs
* Create a new ACL
* Delete an existing ACL


How to Choose the Number of partitions for a given topic?
=========================================================

Text copied from `Confluent's Blog <https://www.confluent.io/blog/how-to-choose-the-number-of-topicspartitions-in-a-kafka-cluster/>`_.

More Partitions Lead to Higher Throughput
-----------------------------------------

The first thing to understand is that a topic partition is the unit of parallelism in the DXL Streaming
Platform. On both the producer and the broker side, writes to different partitions can be done fully in
parallel. So expensive operations such as compression can utilize more hardware resources. On the consumer
side, the streaming platform always gives a single partition’s data to one consumer thread. Thus, the
degree of parallelism in the consumer (within a consumer group) is bounded by the number of partitions being
consumed. **Therefore, in general, the more partitions there are in a cluster, the higher the throughput one
can achieve.**


A rough formula for picking the number of partitions is based on throughput. You measure the throughout
that you can achieve on a single partition for production (call it p) and consumption (call it c).
Let’s say your target throughput is t. Then you need to have at least max(t/p, t/c) partitions.
The per-partition throughput that one can achieve on the producer depends on configurations such as the
batching size, compression codec, type of acknowledgement, replication factor, etc. However, in general, one
can produce at 10s of MB/sec on just a single partition as shown in this
`benchmark <https://engineering.linkedin.com/kafka/benchmarking-apache-kafka-2-million-writes-second-three-cheap-machines>`_.
The consumer throughput is often application dependent since it corresponds to how fast the consumer logic
can process each message. So, you really need to measure it.


Although it’s possible to increase the number of partitions over time, one has to be careful if messages
are produced with keys. When publishing a keyed message, SDKs deterministically maps the message to a
partition based on the hash of the key. This provides a guarantee that messages with the same key are always
routed to the same partition. This guarantee can be important for certain applications since messages within
a partition are always delivered in order to the consumer. If the number of partitions changes, such a
guarantee may no longer hold. To avoid this situation, a common practice is to over-partition a bit.
Basically, you determine the number of partitions based on a future target throughput, say for one or two
years later. Initially, you can just have a small Kafka cluster based on your current throughput. Over time,
you can add more brokers to the cluster and proportionally move a subset of the existing partitions to the
new brokers (which can be done online). This way, you can keep up with the throughput growth without breaking
the semantics in the application when keys are used.

In addition to throughput, there are a few other factors that are worth considering when choosing the number
of partitions. As you will see, in some cases, having too many partitions may also have negative impact.

More Partitions Requires More Open File Handles
-----------------------------------------------

More Partitions May Increase Unavailability
-------------------------------------------

More Partitions May Increase End-to-end Latency
-----------------------------------------------


Modeling Specific Data Types In Kafka
=====================================

Pure Event Streams
------------------

Kafka’s data model is built to represent event streams. A stream in Kafka is modeled by a topic, which is
the logical name given to that data. Each message has a key, which is used to partition data over the cluster
as well as a body which would contain the record data (in the format you have chosen).

Let’s begin with pure event data—the activities taking place inside the company. In a web company these
might be clicks, impression, and various user actions. FedEx might have package deliveries, package pick ups,
driver positions, notifications, transfers and so on.

These type of events can be represented with a single logical stream per action type. If the event has a
natural primary key you can use that to partition data in Kafka, otherwise the Kafka client will
automatically load balance data for you.

Pure event streams should always be retained by size or time. You can choose to keep a month or 100GB per
stream or whatever policy you define.

Give each event it’s own topic and consumers can always subscribe to multiple such topics to get a mixed
feed when they want that. By having a single schema for each topic you will have a much easier time mapping a
topic to a Hive table in Hadoop, a database table in a relational DB or other structured stores.


Management Service REST API
===========================


Console Tools
=============
