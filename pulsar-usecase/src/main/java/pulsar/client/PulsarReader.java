package pulsar.client;

import org.apache.pulsar.client.api.*;

public class PulsarReader {
    private String pulsarURL = System.getenv("PULSAR_URL")!=null ? System.getenv("PULSAR_URL") : "pulsar://localhost:6650";
    private String pulsarTopic = System.getenv("PULSAR_TOPIC")!=null ? System.getenv("PULSAR_TOPIC") : "persistent://public/default/test-topic";
    private String readerName = System.getenv("READER_NAME")!=null ? System.getenv("READER_NAME") : "reader-1";

    public Reader<String> pulsarReader(MessageId id){
        Reader<String> reader = null;
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(pulsarURL)
                    .build();
            reader = client.newReader(Schema.STRING)
                    .topic(pulsarTopic)
                    .readerName(readerName)
                    .startMessageId(id)
                    .create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public void readMessage(){
        Reader<String> reader = pulsarReader(MessageId.earliest); //create a reader that read from the beginning
        int i = 0;
        MessageId newMsgId = null;
        while(true){
            try {
                Message<String> msg = reader.readNext();
                System.out.printf("Received a message: Key: %s Payload: %s\n",msg.getKey(),msg.getValue());
                i++;
                if(i==5){
                    newMsgId = msg.getMessageId(); //record the messageId so that it can be re-read later
                }
                if(i==10){
                    System.out.println("We have read 10 messages, we will re-read the last 5 messages");
                    reader = pulsarReader(newMsgId); //reset the reading position by create a new reader started from the position which was recorded before
                    i=0;
                }
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }
}
