Chapter 1
*********

Section 1
=========

Subsection 1.1
--------------

The purpose of this document is to document the rationale behind Intel Security Data Bus and its architecture
along with its components and interfaces making sure it meets the goals and concerns of the stakeholders.

This messaging platform is intended to serve three primary uses:

* Messaging: The messaging platform routes messages between any application, end point, or other device
  connected to any micro-PoP, PoP, or Data Center.
* Data Integration: The messaging platform captures streams of events or data changes and feeds these to
  other data systems such as relational databases, key-value stores, Hadoop, or the data warehouse.
* Stream processing: It enables continuous, real-time processing and transformation of these streams and
  makes the results available system-wide.

So, the main goals and objectives of the Intel Data Bus project are:

#. To build (and operate) an enterprise-class messaging platform with Apache Kafka and DXL at its core and
   other features to accelerate application development and simplify ongoing operations.

#. Be a team with deep experience working with Apache Kafka, MQTT and related technologies that can advise
   and provide technical assistance to solve challenging messaging, data integration and stream processing
   problems.

#. Contribute and participate in the open source community, maintaining, enhancing, and supporting the open
   source Apache Kafka and other related projects.

Some example taken from a python file

.. literalinclude:: ./examples/example.py

Examples of SVG/EPS figure

.. figure:: ./images/fig1.*
    :alt: figure 1
    :align: center

    This is the caption for figure 1

.. figure:: ./images/fig2.*
    :alt: figure 2
    :align: center

    This is the caption for figure 2


*Intel Security Data Bus* is a solution intended for Intel Security groups (and maybe others) needing a highly
scalable, reliable and low latency solution to satisfy real-time messaging and stream processing needs to
organise and manage massive amounts of data, making it easily accessible in a unified messaging platform
that’s always readily available for many uses throughout the entire organization.

Adding a link

`Databus Javadoc <http://databus-sdk-javadoc.fastdxl.net/1.0.0/>`_

We can also include java code from files

.. literalinclude:: ./examples/ProducerExample.java
    :language: java
    :emphasize-lines: 66,68,75,88-89
    :linenos:

