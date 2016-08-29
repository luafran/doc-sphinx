/*
 *  INTEL CONFIDENTIAL
 *  Copyright 2015 - 2016 Intel Corporation All Rights Reserved.
 *  The source code contained or described herein and all documents related to
 *  the source code ("Material") are owned by Intel Corporation or its suppliers
 *  or licensors. Title to the Material remains with Intel Corporation or its
 *  * suppliers and licensors. The Material contains trade secrets and proprietary
 *  and confidential information of Intel or its suppliers and licensors. The
 *  Material is protected by worldwide copyright and trade secret laws and
 *  treaty provisions. No part of the Material may be used, copied, reproduced,
 *  modified, published, uploaded, posted, transmitted, distributed, or
 *  disclosed in any way without Intel's prior express written permission.
 *
 *  No license under any patent, copyright, trade secret or other intellectual
 *  property right is granted to or conferred upon you by disclosure or delivery
 *  of the Materials, either expressly, by implication, inducement, estoppel or
 *  otherwise. Any license under such intellectual property rights must be
 *  express and approved by Intel in writing.
 * /
 */

package com.intel.databus.examples;

import com.intel.databus.client.common.RecordMetadata;
import com.intel.databus.client.common.internal.builder.TopicNameBuilder;
import com.intel.databus.client.entities.Headers;
import com.intel.databus.client.entities.MessagePayload;
import com.intel.databus.client.entities.RoutingData;
import com.intel.databus.client.producer.Callback;
import com.intel.databus.client.producer.Producer;
import com.intel.databus.client.producer.ProducerRecord;

import java.util.concurrent.TimeUnit;


/**
 * Created by hugo on 3/14/16.
 */
public final class ProducerExample extends Thread {

    private static final long SLEEP_5000 = 5000;
    private static final long SLEEP_2000 = 2000;
    private final String topic;
    private final String tenantGroup;
    private Producer<MyMessage> producer = null;

    ProducerExample(Producer<MyMessage> producer, String topic, String tenantGroup) {
        this.topic = topic;
        this.producer = producer;
        this.tenantGroup = tenantGroup;
    }

    public void run() {

        System.out.println("=======START PRODUCER======  sending to topic:" + TopicNameBuilder.getTopicName(topic,tenantGroup));

        try {
            Thread.sleep(SLEEP_5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int messageNo = 1;

        while (!Thread.interrupted()) {

            RoutingData routingData = new RoutingData(topic,new StringBuilder().append(messageNo).toString(),tenantGroup );

            Headers headers = null; //new Headers();
            //headers.put(HeadersField.SOURCE_ID, UUID.randomUUID().toString());
            //headers.put(HeadersField.TENANT_ID, UUID.randomUUID().toString());

            long startTime = System.nanoTime();
            String message = "Message number" + messageNo + " start time:" + startTime;

            MessagePayload<MyMessage> payload = new MessagePayload<>(new MyMessage(message));

            StringBuilder hdrs = new StringBuilder().append("[");
            if(headers!=null){
                headers.getAll().forEach( (k,v) -> hdrs.append("[" + k +":"+ v +"]"));
            }
            hdrs.append("]");


            try {
                System.out.println("SEND MSG --> TOPIC:"+ TopicNameBuilder.getTopicName(topic,tenantGroup) +
                        " KEY:"+ routingData.getShardingKey() +" HEADERS:" + hdrs + " PAYLOAD:"+ payload.getPayload().getMessage());

                ProducerRecord<MyMessage> record = new ProducerRecord<>(routingData,headers,payload);
                producer.send(record, new DemoCallBack(startTime, messageNo, message));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ++messageNo;
            try {
                Thread.sleep(SLEEP_2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}

class DemoCallBack implements Callback {

    private long startTime;
    private int key;
    private String message;

    public DemoCallBack(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long latencyNS = System.nanoTime() - startTime;
        if (metadata != null) {

            long latency = TimeUnit.MILLISECONDS.convert(latencyNS, TimeUnit.NANOSECONDS);

            System.out.println(
                    "MSG SENT --> TOPIC:"+ metadata.topic() +" KEY:" + key + " PARTITION:" + metadata.partition() +
                            " OFFSET:" + metadata.offset() + " LATENCY:" + latency + " ms");
        } else {
            exception.printStackTrace();
        }

    }
}

