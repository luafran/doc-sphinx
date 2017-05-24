.. _producing:

Producing APIs
**************

Producing APIs allow applications to send streams of data to topics in the DXL Streaming Platform.

The Producer
============

Load Balancing
--------------

The producer sends data directly to the broker that is the leader for the partition without any intervening
routing tier. To help the producer do this all DXL streaming nodes in a cluster can answer a request for
metadata about which servers are alive and where the leaders for the partitions of a topic are at any given
time to allow the producer to appropriately direct its requests.

The client controls which partition it publishes messages to. This can be done at random, implementing a kind
of random load balancing, or it can be done by some semantic partitioning function. We expose the interface
for semantic partitioning by allowing the user to specify a key to partition by and using this to hash to a
partition (there is also an option to override the partition function if needed). For example, if the key
chosen was a user id, then all data for a given user would be sent to the same partition. This in turn will
allow consumers to make locality assumptions about their consumption. This style of partitioning is
explicitly designed to allow locality-sensitive processing in consumers.

Asynchronous Send
-----------------

Batching is one of the big drivers of efficiency, and to enable batching the Kafka producer will attempt to
accumulate data in memory and to send out larger batches in a single request. The batching can be configured
to accumulate no more than a fixed number of messages and to wait no longer than some fixed latency bound
(say 64k or 10 ms). This allows the accumulation of more bytes to send, and few larger I/O operations on the
servers. This buffering is configurable and gives a mechanism to trade off a small amount of additional
latency for better throughput.

Details on configuration and the api for different producers can be found elsewhere in the documentation.


.. _producing-inside:

Producing from Applications Running Inside Cloud
================================================

Java SDK
--------

Please refer to Java SDK documentation.

.. |javadocsdk| raw:: html

   <a href='http://databus-doc.fastdxl.net/3.1/sdk-javadoc/index.html?com/intel/databus/client/producer/DatabusProducer.html' target='_blank'>Java SDK Producer documentation</a>

|javadocsdk|

