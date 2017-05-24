.. _introduction:

Introduction
************

What Can I Do With a Streaming Platform?
========================================

A streaming platform has three core capabilities:

#. It lets you publish and subscribe to streams of records. In this respect it is similar to a message
   queue or enterprise messaging system.
#. It lets you store streams of records in a fault-tolerant way.
#. It lets you process streams of records as they occur.


What is a streaming platform good for?

A Streaming platform is used for two broad classes of application:

#. **Data Integration.** Building real-time streaming data pipelines that reliably get data between systems
   or applications.
#. **Stream Processing.** Building real-time streaming applications that transform or react to the streams of
   data.

See :ref:`use_cases` for a detailed list of examples where a streaming platform is used.


DXL Streaming Platform Concepts
===============================

To understand the DXL Streaming Platform, it is important to explain some concepts first.

* The streaming platform is a distributed system running in a cluster with several servers.
* The cluster stores streams of records in categories called topics.
* Each topic is composed of one or more replicated partitions.
* Each record consists of a key, a list of headers, and a payload.


This is a high level overview of the components part of DXL Streaming solution.

.. figure:: ./images/architecture-3.1.*
    :alt: DXL Architecture (3.1)
    :align: center


The DXL Streaming Platform has several points of interaction and APIs:

* :ref:`producing`. These APIs allow an application to publish a stream of records to one or more topics.
* :ref:`consuming`. These APIs allow an application to subscribe to one or more topics and process the
  stream of records produced to them.
* :ref:`management`. These APIs allow users and administrators to perform different kind of administrative
  and setup operations.
* :ref:`operations`. These APIs are intended for system operators and administrators to monitor,
  operate and support the platform.


.. figure:: ./images/dxl-streaming-apis.*
    :alt: DXL Streaming APIs
    :align: center

    ..

Topics and Logs
---------------

A topic is a category or feed name to which records are published. Topics in the DXL Streaming Platform are
always multi-subscriber; that is, a topic can have zero, one, or many consumers that subscribe to the
data written to it.

For each topic, the DXL Streaming Platform cluster maintains a partitioned log that looks like this:

.. figure:: ./images/topic-anatomy.*
    :alt: Anatomy of a topic
    :align: center

    ..

Each partition is an ordered, immutable sequence of records that is continually appended to—a
structured commit log. The records in the partitions are each assigned a sequential id number called the
offset that uniquely identifies each record within the partition.

The DXL Streaming Platform retains all published records—whether or not they have been consumed—using a
configurable retention period. For example, if the retention policy is set to two days, then for the two
days after a record is published, it is available for consumption, after which it will be discarded to
free up space. The DXL Streaming Platform performance is effectively constant with respect to data size so
storing data for a long time is not a problem.

.. figure:: ./images/log-consumer.*
    :scale: 60 %
    :alt: Log consumer
    :align: center

    ..

In fact, the only metadata retained on a per-consumer basis is the offset or position of that consumer in
the log. This offset is controlled by the consumer: normally a consumer will advance its offset linearly
as it reads records, but, in fact, since the position is controlled by the consumer it can consume records
in any order it likes. For example a consumer can reset to an older offset to reprocess data from the past
or skip ahead to the most recent record and start consuming from "now".

This combination of features means that Kafka consumers are very cheap, they can come and go without much
impact on the cluster or on other consumers. For example, you can use the command line tools to "tail"
the contents of any topic without changing what is consumed by any existing consumers.

The partitions in the log serve several purposes.

* First, they allow the log to scale beyond a size that will fit on a single server.
  Each individual partition must fit on the servers that host it, but a topic may have many partitions
  so it can handle an arbitrary amount of data.

* Second they act as the unit of parallelism, more on that later.


Distribution
------------

The partitions of the log are distributed over the servers in the DXL Streaming Platform with each server
handling data and requests for a share of the partitions. Each partition is replicated across a configurable
number of servers for fault tolerance.

Each partition has one server which acts as the "leader" and zero or more servers which act as "followers".
The leader handles all read and write requests for the partition while the followers passively replicate the
leader. If the leader fails, one of the followers will automatically become the new leader. Each server acts
as a leader for some of its partitions and a follower for others so load is well balanced within the cluster.

Producers
---------

Producers publish data to the topics of their choice. The producer is responsible for choosing which record
to assign to which partition within the topic. This can be done in a round-robin fashion simply to balance
load or it can be done according to some semantic partition function (say based on some key in the record).
More on the use of partitioning in a second!

Consumers
---------

Consumers label themselves with a consumer group name, and each record published to a topic is delivered to
one consumer instance within each subscribing consumer group. Consumer instances can be in separate
processes or on separate machines.

If all the consumer instances have the same consumer group, then the records will effectively be load
balanced over the consumer instances.

If all the consumer instances have different consumer groups, then each record will be broadcast to all
the consumer processes.

TODO: Diagram consumers and consumer groups

A two server Kafka cluster hosting four partitions (P0-P3) with two consumer groups. Consumer group A has
two consumer instances and group B has four.

More commonly, however, we have found that topics have a small number of consumer groups, one for each
"logical subscriber". Each group is composed of many consumer instances for scalability and fault tolerance.
This is nothing more than publish-subscribe semantics where the subscriber is a cluster of consumers
instead of a single process.

The way consumption is implemented in thr DXL Streaming Platform is by dividing up the partitions in the log
over the consumer instances so that each instance is the exclusive consumer of a "fair share" of partitions
at any point in time. This process of maintaining membership in the group is handled by the Kafka protocol
dynamically. If new instances join the group they will take over some partitions from other members of the
group; if an instance dies, its partitions will be distributed to the remaining instances.

The DXL Streaming Platform only provides a total order over records within a partition, not between different
partitions in a topic. Per-partition ordering combined with the ability to partition data by key is
sufficient for most applications. However, if you require a total order over records this can be achieved
with a topic that has only one partition, though this will mean only one consumer process per consumer group.

A DXL Streaming Platform consumer works by issuing "fetch" requests to the brokers leading the partitions it
wants to consume. The consumer specifies its offset in the log with each request and receives back a chunk
of log beginning from that position. The consumer thus has significant control over this position and can
rewind it to re-consume data if need be.

Push vs. Pull
-------------

The DXL Streaming Platform follows a traditional design approach, shared by most messaging systems, where
data is pushed to cluster from the producer and pulled from the cluster by the consumer. Some logging-centric
systems, such as `Scribe <http://github.com/facebook/scribe>`_ and `Apache Flume <http://flume.apache.org/>`_
follow a very different push based path where data is pushed downstream. There are pros and cons to both
approaches. However a push-based system has difficulty dealing with diverse consumers as the broker controls
the rate at which data is transferred. The goal is generally for the consumer to be able to consume at the
maximum possible rate; unfortunately in a push system this means the consumer tends to be overwhelmed when
its rate of consumption falls below the rate of production (a denial of service attack, in essence).
A pull-based system has the nicer property that the consumer simply falls behind and catches up when it can.
This can be mitigated with some kind of backoff protocol by which the consumer can indicate it is overwhelmed,
but getting the rate of transfer to fully utilize (but never over-utilize) the consumer is trickier than it
seems. Previous attempts at building systems in this fashion led us to go with a more traditional pull model.

Another advantage of a pull-based system is that it lends itself to aggressive batching of data sent to the
consumer. A push-based system must choose to either send a request immediately or accumulate more data and
then send it later without knowledge of whether the downstream consumer will be able to immediately
process it. If tuned for low latency this will result in sending a single message at a time only for the
transfer to end up being buffered anyway, which is wasteful. A pull-based design fixes this as the consumer
always pulls all available messages after its current position in the log (or up to some configurable
max size). So one gets optimal batching without introducing unnecessary latency.

The deficiency of a naive pull-based system is that if the broker has no data the consumer may end up
polling in a tight loop, effectively busy-waiting for data to arrive. To avoid this we have parameters in our
pull request that allow the consumer request to block in a "long poll" waiting until data arrives (and
optionally waiting until a given number of bytes is available to ensure large transfer sizes).

Consumer Position
-----------------

Keeping track of what has been consumed is, surprisingly, one of the key performance points of a
messaging system.

Most messaging systems keep metadata about what messages have been consumed on the broker. That is, as a
message is handed out to a consumer, the broker either records that fact locally immediately or it may wait
for acknowledgement from the consumer. This is a fairly intuitive choice, and indeed for a single machine
server it is not clear where else this state could go. Since the data structures used for storage in many
messaging systems scale poorly, this is also a pragmatic choice, since the broker knows what is consumed it
can immediately delete it, keeping the data size small.

What is perhaps not obvious is that getting the broker and consumer to come into agreement about what has
been consumed is not a trivial problem. If the broker records a message as **consumed** immediately every time
it is handed out over the network, then if the consumer fails to process the message (say because it crashes
or the request times out or whatever) that message will be lost. To solve this problem, many messaging
systems add an acknowledgement feature which means that messages are only marked as **sent** not **consumed**
when they are sent; the broker waits for a specific acknowledgement from the consumer to record the message
as **consumed**. This strategy fixes the problem of losing messages, but creates new problems. First of all,
if the consumer processes the message but fails before it can send an acknowledgement then the message will
be consumed twice. The second problem is around performance, now the broker must keep multiple states about
every single message (first to lock it so it is not given out a second time, and then to mark it as
permanently consumed so that it can be removed). Tricky problems must be dealt with, like what to do with
messages that are sent but never acknowledged.

The DXL Streaming Platform handles this differently. Our topic is divided into a set of totally ordered
partitions, each of which is consumed by exactly one consumer within each subscribing consumer group at any
given time. This means that the position of a consumer in each partition is just a single integer, the offset
of the next message to consume. This makes the state about what has been consumed very small, just one number
for each partition. This state can be periodically checkpointed. This makes the equivalent of message
acknowledgements very cheap.

There is a side benefit of this decision. A consumer can deliberately rewind back to an old offset and
re-consume data. This violates the common contract of a queue, but turns out to be an essential feature for
many consumers. For example, if the consumer code has a bug and is discovered after some messages are
consumed, the consumer can re-consume those messages once the bug is fixed.

Guarantees
----------

At a high-level the DXL Streaming Platform gives the following guarantees:

* Messages sent by a producer to a particular topic partition will be appended in the order they are sent.
  That is, if a record M1 is sent by the same producer as a record M2, and M1 is sent first, then M1 will
  have a lower offset than M2 and appear earlier in the log.
* A consumer instance sees records in the order they are stored in the log.
* For a topic with replication factor N, we will tolerate up to N-1 server failures without losing any
  records committed to the log.


Message Delivery Semantics
--------------------------

Now that we understand a little about how producers and consumers work, let's discuss the semantic guarantees
that the DXL Streaming Platform provides between producer and consumer. Clearly there are multiple possible
message delivery guarantees that could be provided:

* *At most once*. Messages may be lost but are never redelivered.

* *At least once*. Messages are never lost but may be redelivered.

* *Exactly once*. This is what people actually want, each message is delivered once and only once.

It's worth noting that this breaks down into two problems: the durability guarantees for publishing a message
and the guarantees when consuming a message.

Many systems claim to provide "exactly once" delivery semantics, but it is important to read the fine print,
most of these claims are misleading (i.e. they don't translate to the case where consumers or producers can
fail, cases where there are multiple consumer processes, or cases where data written to disk can be lost).

DXL Streaming Platform's semantics are straight-forward. When publishing a message we have a notion of the
message being "committed" to the log. Once a published message is committed it will not be lost as long as
one broker that replicates the partition to which this message was written remains "alive". The definition of
alive as well as a description of which types of failures we attempt to handle will be described in more
detail in the next section. For now let's assume a perfect, lossless broker and try to understand the
guarantees to the producer and consumer. If a producer attempts to publish a message and experiences a
network error it cannot be sure if this error happened before or after the message was committed. This is
similar to the semantics of inserting into a database table with an autogenerated key.

These are not the strongest possible semantics for publishers. Although we cannot be sure of what happened in
the case of a network error, it is possible to allow the producer to generate a sort of "primary key" that
makes retrying the produce request idempotent. This feature is not trivial for a replicated system because of
course it must work even (or especially) in the case of a server failure. With this feature it would suffice
for the producer to retry until it receives acknowledgement of a successfully committed message at which
point we would guarantee the message had been published exactly once. We hope to add this in a future
version of the DXL Streaming Platform.

Not all use cases require such strong guarantees. For uses which are latency sensitive we allow the producer
to specify the durability level it desires. If the producer specifies that it wants to wait on the message
being committed this can take on the order of 10 ms. However the producer can also specify that it wants to
perform the send completely asynchronously or that it wants to wait only until the leader (but not
necessarily the followers) have the message.

Now let's describe the semantics from the point-of-view of the consumer. All replicas have the exact same log
with the same offsets. The consumer controls its position in this log. If the consumer never crashed it could
just store this position in memory, but if the consumer fails and we want this topic partition to be taken
over by another process the new process will need to choose an appropriate position from which to start
processing. Let's say the consumer reads some messages -- it has several options for processing the messages
and updating its position.

#. It can read the messages, then save its position in the log, and finally process the messages. In this
   case there is a possibility that the consumer process crashes after saving its position but before saving
   the output of its message processing. In this case the process that took over processing would start at
   the saved position even though a few messages prior to that position had not been processed. This
   corresponds to "at-most-once" semantics as in the case of a consumer failure messages may not be processed.

#. It can read the messages, process the messages, and finally save its position. In this case there is a
   possibility that the consumer process crashes after processing messages but before saving its position.
   In this case when the new process takes over the first few messages it receives will already have been
   processed. This corresponds to the "at-least-once" semantics in the case of consumer failure. In many
   cases messages have a primary key and so the updates are idempotent (receiving the same message twice just
   overwrites a record with another copy of itself).

#. So what about exactly once semantics (i.e. the thing you actually want)? The limitation here is not
   actually a feature of the messaging system but rather the need to co-ordinate the consumer's position with
   what is actually stored as output. The classic way of achieving this would be to introduce a two-phase
   commit between the storage for the consumer position and the storage of the consumers output. But this can
   be handled more simply and generally by simply letting the consumer store its offset in the same place as
   its output. This is better because many of the output systems a consumer might want to write to will not
   support a two-phase commit. As an example of this, our Hadoop ETL that populates data in HDFS stores its
   offsets in HDFS with the data it reads so that it is guaranteed that either data and offsets are both
   updated or neither is. We follow similar patterns for many other data systems which require these stronger
   semantics and for which the messages do not have a primary key to allow for de-duplication.

So effectively the DXL Streaming Platform guarantees at-least-once delivery by default and allows the user to
implement at most once delivery by disabling retries on the producer and committing its offset prior to
processing a batch of messages. Exactly-once delivery requires co-operation with the destination storage
system but Kafka provides the offset which makes implementing this straight-forward.

Replication
-----------

The DXL Streaming Platform replicates the log for each topic's partitions across a configurable number of
servers (you can set this replication factor on a topic-by-topic basis). This allows automatic fail over to
these replicas when a server in the cluster fails so messages remain available in the presence of failures.

The unit of replication is the topic partition. Under non-failure conditions, each partition in Kafka has
a single leader and zero or more followers. The total number of replicas including the leader constitute
the replication factor. All reads and writes go to the leader of the partition. Typically, there are
many more partitions than brokers and the leaders are evenly distributed among brokers. The logs on the
followers are identical to the leader's log—all have the same offsets and messages in the same order
(though, of course, at any given time the leader may have a few as-yet unreplicated messages at the end
of its log).

Followers consume messages from the leader just as a normal Kafka consumer would and apply them to their own
log. Having the followers pull from the leader has the nice property of allowing the follower to naturally
batch together log entries they are applying to their log.

As with most distributed systems automatically handling failures requires having a precise definition of what
it means for a node to be "alive". For Kafka node liveness has two conditions

A node must be able to maintain its session with ZooKeeper (via ZooKeeper's heartbeat mechanism)

If it is a slave it must replicate the writes happening on the leader and not fall "too far" behind

We refer to nodes satisfying these two conditions as being "in sync" to avoid the vagueness of "alive" or
"failed". The leader keeps track of the set of "in sync" nodes. If a follower dies, gets stuck, or falls
behind, the leader will remove it from the list of in sync replicas. The determination of stuck and lagging
replicas is controlled by the replica.lag.time.max.ms configuration.

In distributed systems terminology we only attempt to handle a "fail/recover" model of failures where nodes
suddenly cease working and then later recover (perhaps without knowing that they have died). Kafka does not
handle so-called "Byzantine" failures in which nodes produce arbitrary or malicious responses (perhaps due
to bugs or foul play).

A message is considered "committed" when all in sync replicas for that partition have applied it to their log.
Only committed messages are ever given out to the consumer. This means that the consumer need not worry about
potentially seeing a message that could be lost if the leader fails. Producers, on the other hand, have the
option of either waiting for the message to be committed or not, depending on their preference for tradeoff
between latency and durability. This preference is controlled by the acks setting that the producer uses.

The guarantee that Kafka offers is that a committed message will not be lost, as long as there is at least
one in sync replica alive, at all times.

Kafka will remain available in the presence of node failures after a short fail-over period, but may not
remain available in the presence of network partitions.

Availability and Durability Guarantees
--------------------------------------

When writing to the DXL Streaming Platform, producers can choose whether they wait for the message to be
acknowledged by 0,1 or all (-1) replicas. Note that "acknowledgement by all replicas" does not guarantee that
the full set of assigned replicas have received the message. By default, when acks=all, acknowledgement
happens as soon as all the current in-sync replicas have received the message. For example, if a topic is
configured with only two replicas and one fails (i.e., only one in sync replica remains), then writes that
specify acks=all will succeed. However, these writes could be lost if the remaining replica also fails.
Although this ensures maximum availability of the partition, this behavior may be undesirable to some users
who prefer durability over availability. Therefore, we provide two topic-level configurations that can be
used to prefer message durability over availability:

#. Disable unclean leader election - if all replicas become unavailable, then the partition will remain
   unavailable until the most recent leader becomes available again. This effectively prefers unavailability
   over the risk of message loss. See the previous section on Unclean Leader Election for clarification.

#. Specify a minimum ISR size - the partition will only accept writes if the size of the ISR is above a
   certain minimum, in order to prevent the loss of messages that were written to just a single replica,
   which subsequently becomes unavailable. This setting only takes effect if the producer uses acks=all and
   guarantees that the message will be acknowledged by at least this many in-sync replicas. This setting
   offers a trade-off between consistency and availability. A higher setting for minimum ISR size guarantees
   better consistency since the message is guaranteed to be written to more replicas which reduces the
   probability that it will be lost. However, it reduces availability since the partition will be unavailable
   for writes if the number of in-sync replicas drops below the minimum threshold.

Log Compaction
--------------

Log compaction ensures that Kafka will always retain at least the last known value for each message key
within the log of data for a single topic partition. It addresses use cases and scenarios such as restoring
state after application crashes or system failure, or reloading caches after application restarts during
operational maintenance. Let's dive into these use cases in more detail and then describe how compaction works.

So far we have described only the simpler approach to data retention where old log data is discarded after a
fixed period of time or when the log reaches some predetermined size. This works well for temporal event data
such as logging where each record stands alone. However an important class of data streams are the log of
changes to keyed, mutable data (for example, the changes to a database table).

Let's start by looking at a few use cases where this is useful, then we'll see how it can be used.

#. Database change subscription. It is often necessary to have a data set in multiple data systems, and often
   one of these systems is a database of some kind (either a RDBMS or perhaps a new-fangled key-value store).
   For example you might have a database, a cache, a search cluster, and a Hadoop cluster. Each change to the
   database will need to be reflected in the cache, the search cluster, and eventually in Hadoop. In the case
   that one is only handling the real-time updates you only need recent log. But if you want to be able to
   reload the cache or restore a failed search node you may need a complete data set.

#. Event sourcing. This is a style of application design which co-locates query processing with application
   design and uses a log of changes as the primary store for the application.

#. Journaling for high-availability. A process that does local computation can be made fault-tolerant by
   logging out changes that it makes to its local state so another process can reload these changes and carry
   on if it should fail. A concrete example of this is handling counts, aggregations, and other
   "group by"-like processing in a stream query system. Samza, a real-time stream-processing framework, uses
   this feature for exactly this purpose.

In each of these cases one needs primarily to handle the real-time feed of changes, but occasionally, when a
machine crashes or data needs to be re-loaded or re-processed, one needs to do a full load. Log compaction
allows feeding both of these use cases off the same backing topic.

The general idea is quite simple. If we had infinite log retention, and we logged each change in the above
cases, then we would have captured the state of the system at each time from when it first began. Using
this complete log, we could restore to any point in time by replaying the first N records in the log.
This hypothetical complete log is not very practical for systems that update a single record many times
as the log will grow without bound even for a stable dataset. The simple log retention mechanism which
throws away old updates will bound space but the log is no longer a way to restore the current state—now
restoring from the beginning of the log no longer recreates the current state as old updates may not be
captured at all.

Log compaction is a mechanism to give finer-grained per-record retention, rather than the coarser-grained
time-based retention. The idea is to selectively remove records where we have a more recent update with the
same primary key. This way the log is guaranteed to have at least the last state for each key.

This retention policy can be set per-topic, so a single cluster can have some topics where retention is
enforced by size or time and other topics where retention is enforced by compaction.

Here is a high-level picture that shows the logical structure of a Kafka log with the offset for each message.

.. figure:: ./images/log-cleaner-anatomy.*
    :alt: Structure of a log
    :align: center

    Structure of a log


DXL Streaming Platform as a Messaging System
--------------------------------------------

How does DXL Streaming Platform notion of streams compare to a traditional enterprise messaging system?

Messaging traditionally has two models: `queuing <https://en.wikipedia.org/wiki/Message_queue>`_ and
`publish-subscribe <https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern>`_. In a queue, a pool
of consumers may read from a server and each record goes to one of them; in publish-subscribe the record
is broadcast to all consumers. Each of these two models has a strength and a weakness. The strength of
queuing is that it allows you to divide up the processing of data over multiple consumer instances, which
lets you scale your processing. Unfortunately, queues aren't multi-subscriber, once one process reads the
data it's gone. Publish-subscribe allows you broadcast data to multiple processes, but has no way of scaling
processing since every message goes to every subscriber.

The consumer group concept in the DXL Streaming Platform generalizes these two concepts. As with a queue the
consumer group allows you to divide up processing over a collection of processes (the members of the consumer
group). As with publish-subscribe, Kafka allows you to broadcast messages to multiple consumer groups.

The advantage of DXL Streaming Platform's model is that every topic has both these properties, it can
scale processing and is also multi-subscriber, there is no need to choose one or the other.

The DXL Streaming Platform has stronger ordering guarantees than a traditional messaging system, too.

A traditional queue retains records in-order on the server, and if multiple consumers consume from the queue
then the server hands out records in the order they are stored. However, although the server hands out
records in order, the records are delivered asynchronously to consumers, so they may arrive out of order on
different consumers. This effectively means the ordering of the records is lost in the presence of parallel
consumption. Messaging systems often work around this by having a notion of "exclusive consumer" that allows
only one process to consume from a queue, but of course this means that there is no parallelism in processing.

The DXL Streaming Platform does it better. By having a notion of parallelism, the partition within the
topics, the DXL Streaming Platform is able to provide both ordering guarantees and load balancing over a pool
of consumer processes. This is achieved by assigning the partitions in the topic to the consumers in the
consumer group so that each partition is consumed by exactly one consumer in the group. By doing this we
ensure that the consumer is the only reader of that partition and consumes the data in order. Since there are
many partitions this still balances the load over many consumer instances. Note however that there cannot be
more consumer instances in a consumer group than partitions.

DXL Streaming Platform as a Storage System
------------------------------------------

Any message queue that allows publishing messages decoupled from consuming them is effectively acting as a
storage system for the in-flight messages. What is different about the DXL Streaming Platform is that it is
a very good storage system.

Data written to the DXL Streaming Platform is written to disk and replicated for fault-tolerance. The DXL
Streaming Platform allows producers to wait on acknowledgement so that a write isn't considered complete
until it is fully replicated and guaranteed to persist even if the server written to fails.

The disk structures used by the DXL Streaming Platform scale well, the platform will perform the same whether
you have 50 KB or 50 TB of persistent data on the server.

As a result of taking storage seriously and allowing the clients to control their read position, you can
think of the DXL Streaming Platform as a kind of special purpose distributed filesystem dedicated to
high-performance, low-latency commit log storage, replication, and propagation.

Putting the Pieces Together
---------------------------

This combination of messaging and storage may seem unusual but it is essential to the DXL Streaming Platform's
role as a streaming platform.

A distributed file system like HDFS allows storing static files for batch processing. Effectively a system
like this allows storing and processing historical data from the past.

A traditional enterprise messaging system allows processing future messages that will arrive after you
subscribe. Applications built in this way process future data as it arrives.

The DXL Streaming Platform combines both of these capabilities, and the combination is critical both for
DXL Streaming usage as a platform for streaming applications as well as for streaming data pipelines.

By combining storage and low-latency subscriptions, streaming applications can treat both past and future
data the same way. That is a single application can process historical, stored data but rather than ending
when it reaches the last record it can keep processing as future data arrives. This is a generalized notion
of stream processing that subsumes batch processing as well as message-driven applications.

Likewise for streaming data pipelines the combination of subscription to real-time events make it possible
to use the DXL Streaming Platform for very low-latency pipelines; but the ability to store data reliably make
it possible to use it for critical data where the delivery of data must be guaranteed or for integration with
offline systems that load data only periodically or may go down for extended periods of time for maintenance.
