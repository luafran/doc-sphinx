.. _consuming:

Consuming APIs
**************

The Consuming APIs allow applications to read streams of data from topics in the DXL Streaming Platform.


The Consumer
============

A DXL Streaming Platform consumer works by issuing "fetch" requests to the brokers leading the partitions it
wants to consume. The consumer specifies its offset in the log with each request and receives back a chunk
of log beginning from that position. The consumer thus has significant control over this position and can
rewind it to re-consume data if need be.


.. _consuming-inside:

Consuming from Applications Running Inside Cloud
================================================

Java SDK
--------

Please refer to Java SDK documentation.

.. |javadocsdk| raw:: html

   <a href='http://databus-doc.fastdxl.net/3.1/sdk-javadoc/index.html?com/intel/databus/client/consumer/DatabusConsumer.html' target='_blank'>Java SDK Consumer documentation</a>

|javadocsdk|

