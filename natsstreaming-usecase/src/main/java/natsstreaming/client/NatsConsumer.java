package natsstreaming.client;

import io.nats.streaming.*;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class NatsConsumer {
    private String durableEnable = System.getenv("DURABLE_ENABLE")!=null ? System.getenv("DURABLE_ENABLE"):"false";
    private String subjectName = System.getenv("SUBJECT_NAME")!=null ? System.getenv("SUBJECT_NAME") : "test";
    private String clientId = System.getenv("CLIENT_ID")!=null ? System.getenv("CLIENT_ID") : "consumer-1";
    private String clusterId = System.getenv("CLUSTER_ID")!=null ? System.getenv("CLUSTER_ID") : "nats-streaming";
    private String natsURL = System.getenv("NATS_URL")!=null ? System.getenv("NATS_URL") : "nats://localhost:4222";
    private String queueName = System.getenv("QUEUE_NAME");
    private String durableName = System.getenv("DURABLE_NAME")!=null ? System.getenv("DURABLE_NAME") : "default-durable";
    private String subscriptionOption = System.getenv("SUBSCRIPTION_OPTION")!=null ? System.getenv("SUBSCRIPTION_OPTION"):"latest";
    private String startPosition = System.getenv("START_POSITION");
    public StreamingConnection streamingConnection() {
        StreamingConnection con = null;
        Options options = new Options.Builder()
                .clientId(clientId)
                .clusterId(clusterId)
                .natsUrl(natsURL)
                .build();
        StreamingConnectionFactory cf = new StreamingConnectionFactory(options);
        try {
            con = cf.createConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void startConsumer(){
        Boolean isDurableEnable = Boolean.parseBoolean(durableEnable);
        if(isDurableEnable){
            consumeMessageDurable();
        }else{
            consumeMessage();
        }
    }

    public void consumeMessage(){
        StreamingConnection con = streamingConnection();
        // Receive all stored values in order
        try {
            if(queueName!=null){
                con.subscribe(subjectName, queueName, new MessageHandler() {
                    public void onMessage(Message m) {
                        try {
                            m.ack();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.printf("Received a message: %s\n", new String(m.getData()));
                    }
                }, subscriptionOptions());
            }
            else {
                con.subscribe(subjectName, new MessageHandler() {
                    public void onMessage(Message m) {
                        try {
                            m.ack();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.printf("Received a message: %s\n", new String(m.getData()));
                    }
                }, subscriptionOptions());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void consumeMessageDurable(){
        StreamingConnection con = streamingConnection();
        // Receive all stored values in order
        try {
            if(queueName!=null){
                con.subscribe(subjectName, queueName, new MessageHandler() {
                    public void onMessage(Message m) {
                        try {
                            m.ack();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.printf("Received a message: %s\n", new String(m.getData()));
                    }
                }, durableSubscriptionOptions());
            }
            else {
                con.subscribe(subjectName, new MessageHandler() {
                    public void onMessage(Message m) {
                        try {
                            m.ack();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.printf("Received a message: %s\n", new String(m.getData()));
                    }
                }, durableSubscriptionOptions());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public SubscriptionOptions subscriptionOptions(){
        SubscriptionOptions option = null;
        switch (subscriptionOption){
            case "latest":
                option = new SubscriptionOptions.Builder().startWithLastReceived().ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                break;
            case "earliest":
                option = new SubscriptionOptions.Builder().deliverAllAvailable().ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                break;
            case "sequence_number":
                try {
                    int start = Integer.parseInt(startPosition);
                    option =  new SubscriptionOptions.Builder().startAtSequence(start).ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("Start position are not specified correctly, must be a number");
                }
                break;
            case "time_delta":
                try {
                    Duration ago = Duration.ofSeconds(Integer.parseInt(startPosition));
                    option = new SubscriptionOptions.Builder().startAtTimeDelta(ago).ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                } catch(NumberFormatException e){
                    throw new IllegalArgumentException("Start position are not specified correctly, must be a number");
                }
                break;
            default:
                throw new IllegalArgumentException("Subscription is not specified correctly");
        }
        return option;
    }

    public SubscriptionOptions durableSubscriptionOptions(){
        SubscriptionOptions option = null;
        switch (subscriptionOption){
            case "latest":
                option = new SubscriptionOptions.Builder().durableName(durableName).startWithLastReceived().ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                break;
            case "earliest":
                option = new SubscriptionOptions.Builder().durableName(durableName).deliverAllAvailable().ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                break;
            case "sequence_number":
                try {
                    int start = Integer.parseInt(startPosition);
                    option =  new SubscriptionOptions.Builder().durableName(durableName).startAtSequence(start).ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("Start position are not specified correctly, must be a number");
                }
                break;
            case "time_delta":
                try {
                    Duration ago = Duration.ofSeconds(Integer.parseInt(startPosition));
                    option = new SubscriptionOptions.Builder().durableName(durableName).startAtTimeDelta(ago).ackWait(Duration.ofSeconds(100)).manualAcks().maxInFlight(1).build();
                } catch(NumberFormatException e){
                    throw new IllegalArgumentException("Start position are not specified correctly, must be a number");
                }
                break;
            default:
                throw new IllegalArgumentException("Subscription is not specified correctly");
        }
        return option;
    }
}
