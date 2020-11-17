package pulsar.main;


import pulsar.client.PulsarConsumer;
import pulsar.client.PulsarProducer;
import pulsar.client.PulsarReader;

public class RunClient {
    public static void main(String[] args){
        String clientMode = System.getenv("CLIENT_MODE");
        String replayTrigger = System.getenv("REPLAY_TRIGGER")!=null ? System.getenv("REPLAY_TRIGGER"):"false";
        Boolean isReplayTrigger = Boolean.parseBoolean(replayTrigger);
        if (clientMode == null) {
            throw new IllegalArgumentException("Client type must be specified: with the environment variable \'CLIENT_MODE\' with either \'producer\' or \'consumer\' or \'reader\'");
        }
        if (clientMode.equals("producer")) {
            PulsarProducer producer = new PulsarProducer();
            producer.publishMessage();
        }
        if (clientMode.equals("consumer")) {
            PulsarConsumer consumer = new PulsarConsumer();
            if(isReplayTrigger){
                consumer.consumeMessageReplay();
            }
            else {
                consumer.consumeMessage();
            }
        }
        if (clientMode.equals("reader")) {
            PulsarReader reader = new PulsarReader();
            reader.readMessage();
        }


    }
}
