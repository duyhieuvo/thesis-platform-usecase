package pulsar.client;

import org.apache.pulsar.client.api.*;

import java.util.Random;

public class PulsarProducer {
    private String pulsarURL = System.getenv("PULSAR_URL")!=null ? System.getenv("PULSAR_URL") : "pulsar://localhost:6650";
    private String pulsarTopic = System.getenv("PULSAR_TOPIC")!=null ? System.getenv("PULSAR_TOPIC") : "persistent://public/default/test-topic";
    private String producerName = System.getenv("PRODUCER_NAME")!=null ? System.getenv("PRODUCER_NAME") : "producer-1";
    private String routingMode = System.getenv("ROUTING_MODE")!=null ? System.getenv("ROUTING_MODE") : "RoundRobinPartition";

    public Producer<String> pulsarProducer(){
        Producer<String> producer = null;
        MessageRoutingMode mode = null;
        if (routingMode.equals("RoundRobinPartition")){
            mode = MessageRoutingMode.RoundRobinPartition;
        }else if(routingMode.equals("SinglePartition")){
            mode = MessageRoutingMode.SinglePartition;
        }else{
            throw new IllegalArgumentException("Routing mode is invalid");
        }
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(pulsarURL)
                    .build();
            producer =  client.newProducer(Schema.STRING)
                    .topic(pulsarTopic)
                    .producerName(producerName)
                    .messageRoutingMode(mode)
                    .batcherBuilder(BatcherBuilder.KEY_BASED)
                    .hashingScheme(HashingScheme.Murmur3_32Hash)
                    .create();

        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

    public void publishMessage(){
        Producer<String> producer = pulsarProducer();
        Random random = new Random();
        long start = 0;
        float elapsed = 0;
        int i = 0;
        int wait = 0;
        while(true){
            String msg = "This is message " + i;
            try {
                producer.newMessage()
                        .key(Integer.toString(i%3))
                        .value(msg)
                        .send();
                System.out.println("Sent: Key: " + Integer.toString(i%3) + " Message: " + msg);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
            i++;
            wait = random.nextInt(5- 1 + 1) + 1;
            start = System.currentTimeMillis();
            while(true){
                elapsed= (System.currentTimeMillis()-start)/1000F;
                if(elapsed>wait){
                    break;
                }
            }
            if(i>100) i=0;
        }
    }
}
