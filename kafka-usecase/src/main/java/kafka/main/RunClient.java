package kafka.main;

import kafka.client.ConsumerKafka;
import kafka.client.ProducerKafka;


public class RunClient {
    public static void main(String[] args){
        String clientMode = System.getenv("CLIENT_MODE");
        String replayTrigger = System.getenv("REPLAY_TRIGGER")!=null ? System.getenv("REPLAY_TRIGGER"):"false";
        Boolean isReplayTrigger = Boolean.parseBoolean(replayTrigger);
        if (clientMode == null) {
            throw new IllegalArgumentException("Client type must be specified: with the environment variable \'CLIENT_MODE\' with either \'producer\' or \'consumer\'");
        }
        if (clientMode.equals("producer")) {
            ProducerKafka producer = new ProducerKafka();
            producer.publishRecords();
        }
        if (clientMode.equals("consumer")) {
            ConsumerKafka consumer = new ConsumerKafka();
            if(isReplayTrigger){
                System.out.println("Replay trigger");
                consumer.consumerReplayRecords();
            }
            else{
                System.out.println("No replay trigger");
                consumer.consumeRecords();
            }
        }

    }
}
