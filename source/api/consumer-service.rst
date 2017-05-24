.. _consumer-service-api:

Consumer Service API
********************

Health Check
============

.. http:get:: /v1/health

   Returns service's status along with other useful information like version and build number.

   **Example Request**

   .. sourcecode:: http

      GET /v1/health HTTP/1.1
      Accept: application/json

   **Example Response**

   .. sourcecode:: http

      HTTP/1.1 200 OK
      Content-Type: application/json

      {
          "data": {
              "build": "000",
              "currentSetting": "Prod",
              "healthy": true,
              "items": null,
              "kind": "healthcheck",
              "name": "getHealthCheck",
              "version": "1.0.4"
          }
      }

   :reqheader Accept: only application/json is supported
   :reqheader Authorization: OAuth token to authenticate
   :resheader Content-Type: only application/json is supported
   :statuscode 200: no error
   :statuscode 401: token not found in Authorization header or querystring
   :statuscode 403: token invalid or expired.
