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

    }

}
