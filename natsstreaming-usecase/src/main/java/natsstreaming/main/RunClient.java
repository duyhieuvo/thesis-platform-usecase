package natsstreaming.main;

import natsstreaming.client.NatsConsumer;
import natsstreaming.client.NatsProducer;

public class RunClient {
    public static void main(String[] args) {
        String clientMode = System.getenv("CLIENT_MODE");
        if (clientMode == null) {
            throw new IllegalArgumentException("Client type must be specified: with the environment variable \'CLIENT_MODE\' with either \'producer\' or \'consumer\'");
        }
        if (clientMode.equals("producer")) {
            NatsProducer producer = new NatsProducer();
            producer.publishMessage();
        }
        if (clientMode.equals("consumer")) {
            NatsConsumer consumer = new NatsConsumer();
            consumer.startConsumer();
        }

    }
}
