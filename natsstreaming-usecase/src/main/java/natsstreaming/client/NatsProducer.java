package natsstreaming.client;

import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class NatsProducer {
    private String subjectName = System.getenv("SUBJECT_NAME")!=null ? System.getenv("SUBJECT_NAME") : "test";
    private String clientId = System.getenv("CLIENT_ID")!=null ? System.getenv("CLIENT_ID") : "producer-1";
    private String clusterId = System.getenv("CLUSTER_ID")!=null ? System.getenv("CLUSTER_ID") : "nats-streaming";
    private String natsURL = System.getenv("NATS_URL")!=null ? System.getenv("NATS_URL") : "nats://localhost:4222";
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

    public void publishMessage(){
        StreamingConnection con = streamingConnection();
        Random random = new Random();
        long start = 0;
        float elapsed = 0;
        int i = 0;
        int wait = 0;
        while(true){
            String msg = "This is message " + i;
            try {
                con.publish(subjectName,msg.getBytes());
                System.out.println("Sent: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
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

