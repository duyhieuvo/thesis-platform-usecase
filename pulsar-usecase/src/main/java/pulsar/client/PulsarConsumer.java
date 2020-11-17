package pulsar.client;

import org.apache.pulsar.client.api.*;

public class PulsarConsumer {
    private String pulsarURL = System.getenv("PULSAR_URL")!=null ? System.getenv("PULSAR_URL") : "pulsar://localhost:6650";
    private String pulsarTopic = System.getenv("PULSAR_TOPIC")!=null ? System.getenv("PULSAR_TOPIC") : "persistent://public/default/test-topic";
    private String consumerName = System.getenv("CONSUMER_NAME")!=null ? System.getenv("CONSUMER_NAME") : "consumer-1";
    private String subscriptionName = System.getenv("SUBSCRIPTION_NAME")!=null ? System.getenv("SUBSCRIPTION_NAME") : "default-subscription";
    private String subscriptionType = System.getenv("SUBSCRIPTION_TYPE")!=null ? System.getenv("SUBSCRIPTION_TYPE") : "Exclusive";
    private String startPosition = System.getenv("START_POSITION")!=null ? System.getenv("START_POSITION") : "Latest";


    public SubscriptionType determineSubscriptionType(){
        SubscriptionType type = null;
        switch (subscriptionType){
            case "Exclusive":
                type = SubscriptionType.Exclusive;
                break;
            case "Failover":
                type = SubscriptionType.Failover;
                break;
            case "Shared":
                type = SubscriptionType.Shared;
                break;
            case "Key_shared":
                type = SubscriptionType.Key_Shared;
                break;
            default:
                throw new IllegalArgumentException("Subscription type is invalid");
        }
        return type;
    }
    public Consumer<String> pulsarConsumer(){
        Consumer<String> consumer = null;
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(pulsarURL)
                    .build();
            consumer =  client.newConsumer(Schema.STRING)
                    .topic(pulsarTopic)
                    .consumerName(consumerName)
                    .subscriptionName(subscriptionName)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.valueOf(startPosition))
                    .subscriptionType(determineSubscriptionType())
                    .subscribe();

        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return consumer;
    }

    public void consumeMessage(){
        Consumer<String> consumer = pulsarConsumer();
        Message<String> message = null;
        while(true){
            try {
                message = consumer.receive();
                System.out.printf("Received a message: Key: %s Payload: %s\n",message.getKey(),message.getValue());
                consumer.acknowledge(message);

            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }

    public void consumeMessageReplay(){
        Consumer<String> consumer = pulsarConsumer();
        Message<String> message = null;
        int i = 0;
        MessageId newMsgId = null;
        while(true){
            try {
                message = consumer.receive();
                System.out.printf("Received a message: Key: %s Payload: %s\n",message.getKey(),message.getValue());
                i++;
                if(i==5){
                    newMsgId = message.getMessageId(); //record the messageId so that it can be re-read later
                }

                consumer.acknowledge(message);
                if(i==10){
                    System.out.println("We have read 10 messages, we will re-read the last 5 messages");
                    consumer.seek(newMsgId); //Reset the cursor of consumer
                    i=0;
                }

            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }
}
