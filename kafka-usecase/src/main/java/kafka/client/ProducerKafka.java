package kafka.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerKafka {
    private String kafkaURL = System.getenv("KAFKA_URL")!=null ? System.getenv("KAFKA_URL") : "localhost:9092";
    private String kafkaTopic = System.getenv("KAFKA_TOPIC")!=null ? System.getenv("KAFKA_TOPIC") : "test-topic";
    private String clientID = System.getenv("CLIENT_ID")!=null ? System.getenv("CLIENT_ID") : "producer-1";

    public KafkaProducer<Integer,String> kafkaProducer(){
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG,clientID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.IntegerSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,1);
        props.put(ProducerConfig.RETRIES_CONFIG,3);
        return new KafkaProducer<Integer, String>(props);
    }

    public void publishRecords(){
        KafkaProducer<Integer,String> producer = kafkaProducer();
        Random random = new Random();
        long start = 0;
        float elapsed = 0;
        int i = 0;
        int wait = 0;
        ProducerRecord<Integer,String> record = null;

        while(true){
            String msg = "This is message " + i;
            record = new ProducerRecord<Integer, String>(kafkaTopic,i%3,msg);
            Future<RecordMetadata> kafkaStatus = producer.send(record);
            try {
                kafkaStatus.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Sent: Key: " + i%3 + " Message: " + msg);



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
