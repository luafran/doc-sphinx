.. _quickstart:

Quickstart
**********

This section explains how to set up a quick local instance of the DXL Streaming Platform that allows
producing and consuming from topics. For the sake of simplicity it is used Docker Compose.

Steps:


#. Install Docker engine and Docker compose.

   Follow instructions for your platform in the following links:

   .. |installdockerengine| raw:: html

      <a href='https://docs.docker.com/engine/installation/' target='_blank'>Install Docker Engine</a>
      <p>
      <a href='https://docs.docker.com/compose/install/' target='_blank'>Install Docker Compose</a>

   |installdockerengine|

   |

#. Add host entries used in Docker Compose to /etc/hosts file.

   As root, edit /etc/hosts file and add these entries.

   .. code-block:: shell

      127.0.0.1       zookeeper-1 kafka-1
      127.0.0.2       zookeeper-2 kafka-2
      127.0.0.3       zookeeper-3 kafka-3

   |

#. Create a Docker Compose file named *docker-compose-dxl-streaming.yml* with the following content.

   .. literalinclude:: ./code/docker-compose-dxl-streaming.yml
        :language: yaml

   |

