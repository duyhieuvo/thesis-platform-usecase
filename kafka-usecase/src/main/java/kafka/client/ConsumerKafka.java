package kafka.client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerKafka {
    private String kafkaURL = System.getenv("KAFKA_URL")!=null ? System.getenv("KAFKA_URL") : "localhost:9092";
    private String kafkaTopic = System.getenv("KAFKA_TOPIC")!=null ? System.getenv("KAFKA_TOPIC") : "test-topic";
    private String clientID = System.getenv("CLIENT_ID")!=null ? System.getenv("CLIENT_ID") : "consumer-1";
    private String groupID = System.getenv("GROUP_ID")!=null ? System.getenv("GROUP_ID") : "consumer-group-1";
    private String offsetReset = System.getenv("OFFSET_RESET")!=null ? System.getenv("OFFSET_RESET") : "latest";

    public KafkaConsumer<Integer,String> kafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientID);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<Integer, String>(props);
    }

    public void consumeRecords(){
        KafkaConsumer<Integer, String> consumer = kafkaConsumer();
        consumer.subscribe(Arrays.asList(kafkaTopic));
        while(true){
            ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<Integer, String> record : records) {
                Integer key = record.key();
                String value = record.value();
                Integer partition = record.partition();
                System.out.printf("Received a message: Key: %s Value: %s Partition: %s\n", key.toString(), value,partition.toString());
            }
            consumer.commitSync(Duration.ofSeconds(5));
        }
    }

    public void consumerReplayRecords(){
        KafkaConsumer<Integer, String> consumer = kafkaConsumer();
        consumer.subscribe(Arrays.asList(kafkaTopic));
        int i = 0;
        long currentOffset = 0;
        int currentParition= 0;
        while(true){
            ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<Integer, String> record : records) {
                i++;
                Integer key = record.key();
                String value = record.value();
                Integer partition = record.partition();
                System.out.printf("Received a message: Key: %s Value: %s Partition: %s\n", key.toString(), value,partition.toString());
                currentOffset = record.offset();
                currentParition = record.partition();
            }
            consumer.commitSync(Duration.ofSeconds(5));
            if(i>=10){
                System.out.println("We have read 10 or more messages, we will re-read the last 5 messages");
                i=0;
                consumer.seek(new TopicPartition(kafkaTopic, currentParition),currentOffset-5);
            }
        }
    }
}
